package dev.rialmar.todoapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

// Constantes para identificar el canal y la notificación
const val CHANNEL_ID = "inactivity_channel"
const val NOTIFICATION_ID = 1001

class NotificacionInactividadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        //Antes de lanzar la notificacion, nos aseguramos de que el canal existe
        crearCanalNotificacion()

        //Ejecutamos la accion de mostrar la notificacion
        mostrarNotificacion()

        return Result.success()
    }

    private fun crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = "Recordatorio de Inactividad"
            val descripcionTexto = "Avisa al usuario cuando lleva tiempo sin añadir tareas"
            val importancia = NotificationManager.IMPORTANCE_HIGH

            val canal = NotificationChannel(CHANNEL_ID, nombre, importancia).apply {
                description = descripcionTexto
                enableVibration(true)
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(canal)
        }
    }

    private fun mostrarNotificacion() {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //Construccion de la notificacion
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("¿Has terminado tus tareas?")
            .setContentText("Hace un rato que no añades ninguna tarea nueva.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Se cierra sola al hacer clic

        //Lanzar la notificacion al sistema
        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }
}