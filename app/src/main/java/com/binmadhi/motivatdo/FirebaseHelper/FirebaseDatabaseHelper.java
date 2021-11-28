package com.binmadhi.motivatdo.FirebaseHelper;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.binmadhi.motivatdo.Models.NotificationModel;
import com.binmadhi.motivatdo.Models.TaskModel;
import com.binmadhi.motivatdo.Models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseDatabaseHelper {
    private static final String TAG = "TAG";
    private final Context context;
    //initialize the firebase database and storage variables and specify roots in String
    private final DatabaseReference tableNotifications = FirebaseDatabase.getInstance().getReference().child("notifications");
    private final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
    private final DatabaseReference tableTasks = FirebaseDatabase.getInstance().getReference().child("tasks");
    private final StorageReference folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
    private final StorageReference folderAttachment = FirebaseStorage.getInstance().getReference().child("attachment_images");

    public FirebaseDatabaseHelper(Context context) {
        this.context = context;
    }

    //load current user info  from database by User I'd
    private void loadUserInfo(String uid, final OnLoadUserInfoCompleteListener listener) {
        try {
            tableUser.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    listener.onLoadUserInfoComplete(dataSnapshot.getValue(User.class));


                    tableUser.removeEventListener(this);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onLoadUserInfoComplete(null);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "loadUserInfo: " + e.toString());
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //save the current user info
    public void saveUserInfo(User user, final OnSaveUserCompleteListener listener) {
        try {

            tableUser.child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        listener.onSaveUserComplete(true);
                    else
                        listener.onSaveUserComplete(false);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "saveUserInfo: " + e.toString());
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    //login function that check the current user info in database and if matches then login
    public void attemptLogin(String email, String password, final OnLoginSignupAttemptCompleteListener listener) {
        try {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        loadUserInfo(FirebaseAuth.getInstance().getCurrentUser().getUid(), new OnLoadUserInfoCompleteListener() {
                            @Override
                            public void onLoadUserInfoComplete(User user) {
                                new PreferencesManager(context).saveCurrentUser(user);
                                listener.onLoginSignupSuccess(user);
                            }
                        });
                    } else
                        listener.onLoginSignupFailure(task.getException().getMessage());

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptLogin: " + e.toString());
            Toast.makeText(context, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //create account function with required parameters
    public void attemptSignUp(final User user, String password, final Uri dpUri, final OnLoginSignupAttemptCompleteListener listener) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        uploadFile(dpUri, folderProfilePics.child(user.getUid() + ".jpg"), new OnUploadFileCompleteListener() {
                            @Override
                            public void onUploadFileComplete(String url) {
                                user.setImage(url);

                                saveUserInfo(user, new OnSaveUserCompleteListener() {
                                    @Override
                                    public void onSaveUserComplete(boolean isSuccessful) {
                                        new PreferencesManager(context).saveCurrentUser(user);
                                        listener.onLoginSignupSuccess(user);
                                    }
                                });
                            }
                        });
                    } else
                        listener.onLoginSignupFailure(task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptSignUp: " + e.toString());
        }
    }

    //upload any type of file like pdf,image etc To Firebase Database
    public void uploadFile(Uri fileUri, final StorageReference path, final OnUploadFileCompleteListener listener) {

        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "onUploadFileComplete: Now uploadiong file");

                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listener.onUploadFileComplete(uri.toString());
                        }
                    });
                }
            }
        });
    }

    public void assignTask(TaskModel taskModel, Uri mainImageUri, OnAssignTaskCompleteListener listener) {
        uploadFile(mainImageUri, folderProfilePics.child(taskModel.getTid() + ".jpg"), new OnUploadFileCompleteListener() {
            @Override
            public void onUploadFileComplete(String url) {
                taskModel.setImage(url);
                tableTasks.child(taskModel.getTid()).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            NotificationModel model = new NotificationModel();
                            String mGroupId = tableNotifications.push().getKey();
                            model.setNid(mGroupId);
                            model.setNotificationTitle(taskModel.getTaskName());
                            model.setMessage("You Are Given a Task");
                            model.setUid(taskModel.getAssignTo());
                            model.setTask_id(taskModel.getTid());
                            model.setImage(taskModel.getImage());
                            tableNotifications.child(mGroupId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        sendNotification(taskModel.getTaskName(), taskModel.getAssignTo(), "You Are Assigned a task", context);
                                        listener.onAssignTaskCompleted("success");
                                    } else {
                                        listener.onAssignTaskCompleted(task.getException().toString());
                                    }
                                }
                            });
                        } else {
                            listener.onAssignTaskCompleted(task.getException().toString());
                        }
                    }
                });
            }
        });

    }

    public void attemptSignUpWithOutImages(User user, String password, OnLoginSignupAttemptCompleteListener listener) {
        try {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                        user.setImage("null");
                        saveUserInfo(user, new OnSaveUserCompleteListener() {
                            @Override
                            public void onSaveUserComplete(boolean isSuccessful) {
                                new PreferencesManager(context).saveCurrentUser(user);
                                listener.onLoginSignupSuccess(user);
                            }
                        });

                    } else
                        listener.onLoginSignupFailure(task.getException().getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptSignUp: " + e.toString());
        }

    }

    public void assignTaskWithoutImage(TaskModel taskModel, Uri mainImageUri, OnAssignTaskCompleteListener listener) {
        taskModel.setImage("null");
        tableTasks.child(taskModel.getTid()).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    NotificationModel model = new NotificationModel();
                    String mGroupId = tableNotifications.push().getKey();
                    model.setNid(mGroupId);
                    model.setNotificationTitle(taskModel.getTaskName());
                    model.setMessage("You Are Given a Task");
                    model.setUid(taskModel.getAssignTo());
                    model.setTask_id(taskModel.getTid());
                    model.setImage(taskModel.getImage());
                    tableNotifications.child(mGroupId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendNotification(taskModel.getTaskName(), taskModel.getAssignTo(), "You Are Assigned a task", context);
                                listener.onAssignTaskCompleted("success");
                            } else {
                                listener.onAssignTaskCompleted(task.getException().toString());
                            }
                        }
                    });
                } else {
                    listener.onAssignTaskCompleted(task.getException().toString());
                }
            }
        });

    }

    public interface OnQueryUserByCategoryCompleteListener {
        void OnQueryUserByCategoryCompleted(ArrayList<User> userArrayList, String message);
    }

    public interface OnSaveUserCompleteListener {
        void onSaveUserComplete(boolean isSuccessful);
    }

    public interface OnAssignTaskCompleteListener {
        void onAssignTaskCompleted(String isSuccessful);
    }

    public interface OnLoadUserInfoCompleteListener {
        void onLoadUserInfoComplete(User user);
    }

    public interface OnLoginSignupAttemptCompleteListener {
        void onLoginSignupSuccess(User user);

        void onLoginSignupFailure(String failureMessage);
    }

    public interface OnUploadFileCompleteListener {
        void onUploadFileComplete(String url);
    }

    public void sendNotification(String title, String topic, String message, final Context context) {

        //  topic = topic.replaceAll("\\s", "");

        RequestQueue mRequestQue;
        mRequestQue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topic.replaceAll("[^A-Za-z0-9]", "-"));
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("data", "dummy");
            notificationObj.put("body", message.replaceAll(" + ", " "));
            //replace notification with data when went send data
            json.put("notification", notificationObj);
            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e(TAG, "onResponse: " + response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "onError: " + error.networkResponse);
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=AAAA6Ye0oYE:APA91bHFT06u110QTRT6tZt_60SejJ2x32LTOOgxqpXlFSk39vgYeLjtj1C5kqSlBQb0QPwg1EJgbQbRr-989XdwLxsyq36mAQIA8B_4-oEeh7O9uaF9A4hwQUdNPeGDFn43083ffuwP");
                    return header;
                }
            };
            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
