package com.nyx_corp.auctaritascodex;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.ExecutionException;

public class FirestoreWorker extends Worker {

    private static final String PREFS_NAME = "FirestorePrefs";
    private static final String LAST_DOC_ID = "last_doc_id";
    public FirestoreWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        try {
            // Use Tasks.await to make the async Firebase call "wait" for the result
            QuerySnapshot querySnapshot = Tasks.await(
                    db.collection("infoCards")
                            .orderBy("createdAt", Query.Direction.DESCENDING) // Use your actual field name here!
                            .limit(1)
                            .get()
            );

            if (!querySnapshot.isEmpty()) {
                DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                String latestDocId = doc.getId();

                SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                String lastDocIdSaved = prefs.getString(LAST_DOC_ID, "");

                if (!latestDocId.equals(lastDocIdSaved)) {
                    String title = doc.getString("title");
                    String desc = doc.getString("description");
                    String urgency = doc.getString("urgency");
                    Object deadline = doc.get("deadline");

                    String messageBody = "[" + urgency + "] " + desc;
                    if (deadline != null) {
                        messageBody += "\nDeadline: " + deadline.toString();
                    }

                    triggerNotification(title, messageBody);

                    // Update the saved ID
                    prefs.edit().putString(LAST_DOC_ID, latestDocId).apply();
                }
            }
            return Result.success();

        } catch (ExecutionException | InterruptedException e) {
            Log.e("FirestoreWorker", "Error fetching data", e);
            return Result.retry(); // Tells WorkManager to try again later
        }
    }

    private void triggerNotification(String title, String content) {
        String channelId = "updates_channel";
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Required for Android 8.0+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Database Updates", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(content) // This still shows when collapsed
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                // Add this section for expansion:
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content)); // The full text shown when expanded
        manager.notify(1, builder.build());
    }
}
