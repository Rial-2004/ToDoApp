package dev.rialmar.todoapp

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

const val CHANNEL_ID = "inactivity_channel"
const val NOTIFICATION_ID = 1001

class NotificacionInactividadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        mostrarNotificacion()
        return Result.success()
    }

    private fun mostrarNotificacion() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Recordatorio de Inactividad"
            val importance = android.app.NotificationManager.IMPORTANCE_HIGH
            val channel = android.app.NotificationChannel(CHANNEL_ID, name, importance)

            channel.description = "Notificaciones que avisan al usuario sobre inactividad."
            channel.enableVibration(true)

            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¿Has terminado tus tareas?")
            .setContentText("Hace rato que no añades ninguna tarea")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}