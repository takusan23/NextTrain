package io.github.takusan23.nexttrain

import android.annotation.TargetApi
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.DrawableRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import io.github.takusan23.nexttrain.DataBase.StationSQLiteHelper
import io.github.takusan23.nexttrain.Scraping.TrainTimeTable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class NextTrainTimeQSTile : TileService() {

    //でーたべーす
    lateinit var helper: StationSQLiteHelper
    lateinit var db: SQLiteDatabase

    //Tileクリック
    override fun onClick() {
        super.onClick()

        //Tileの色変える
        qsTile.state = Tile.STATE_ACTIVE
        qsTile.updateTile()


        //データベース用意
        if (!this@NextTrainTimeQSTile::helper.isInitialized) {
            helper = StationSQLiteHelper(applicationContext)
            db = helper.writableDatabase
            db.disableWriteAheadLogging()
        }

        //並び順で2駅まで取得する
        val cursor = db.query(
            "station_db",
            arrayOf("up", "down", "name"),
            null,
            null,
            null,
            null,
            null
        )
        cursor.moveToFirst()
        GlobalScope.launch {
            for (i in 0 until 2) {
                val up = cursor.getString(0)
                val down = cursor.getString(1)
                //スクレイピングできるまで待つ
                async {
                    val trainTimeTable = TrainTimeTable()
                    trainTimeTable.getNextTrain(
                        up, down
                    )
                    //通知出す
                    showNotification(trainTimeTable, i)
                    //次に進む
                    cursor.moveToNext()
                    //Tileの色変える
                    qsTile.state = Tile.STATE_INACTIVE
                    qsTile.updateTile()
                }.await()
                cursor.moveToNext()
            }
            cursor.close()
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    fun showNotification(trainTimeTable: TrainTimeTable, notifyId: Int) {

        val upTrain =
            "上り：${trainTimeTable.upNextTime} ${trainTimeTable.upTrainType} ${trainTimeTable.upTrainFor}"
        val downTrain =
            "下り：${trainTimeTable.downNextTime} ${trainTimeTable.downTrainType} ${trainTimeTable.downTrainFor}"

        //今の時間。Hour
        val calender = Calendar.getInstance()
        val hour = calender.get(Calendar.HOUR_OF_DAY)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        //通知チャンネル
        val id = "next_train_qs"
        if (notificationManager.getNotificationChannel(id) == null) {
            val channel = NotificationChannel(id, "次の電車の通知", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(applicationContext, id)
            .setSmallIcon(R.drawable.next_train_icon)
            .setContentTitle(trainTimeTable.stationName)
            .setContentText("$upTrain / $downTrain")
            .setStyle(NotificationCompat.InboxStyle().also {

                //スコープ関数？なんかすごい便利
                it.setBigContentTitle("${trainTimeTable.stationName}駅の${hour}時の時刻表")

                //時刻表を表示する。配列の中の配列から今の時刻の時刻表が入った配列を取得している
                for (i in 0 until trainTimeTable.upTrainTimeTableListList.size) {
                    //下りも存在するか？
                    if (trainTimeTable.downTrainTimeTableListList.size > i) {
                        //今の時間の時刻表の配列取得
                        if (trainTimeTable.upTrainTimeTableListHourList[i] == hour.toString()) {
                            val upNextTrainList = trainTimeTable.upTrainTimeTableListList[i]
                            val upNextTypeList = trainTimeTable.upTrainTimeTableListTypeList[i]
                            val upNextForList = trainTimeTable.upTrainTimeTableListForList[i]

                            val downNextTrainList = trainTimeTable.downTrainTimeTableListList[i]
                            val downNextTypeList = trainTimeTable.downTrainTimeTableListTypeList[i]
                            val downNextForList = trainTimeTable.downTrainTimeTableListForList[i]
                            //追加する
                            for (i in 0 until upNextTrainList.size) {
                                //下りも存在するか
                                if (downNextTrainList.size > i) {
                                    //追加する
                                    val upTrain =
                                        "上り $hour:${upNextTrainList[i]} ${upNextTypeList[i]} ${upNextForList[i]}"
                                    val downTrain =
                                        "下り $hour:${downNextTrainList[i]} ${downNextTypeList[i]} ${downNextForList[i]}"

                                    it.addLine("$upTrain | $downTrain")
                                }
                            }
                        }
                    }
                }
            })
            .setVibrate(longArrayOf(100, 0, 100, 0))

        notificationManager.notify(notifyId, notification.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

}