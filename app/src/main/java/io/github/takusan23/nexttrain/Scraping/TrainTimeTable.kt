package io.github.takusan23.nexttrain.Scraping

import android.content.Context
import android.os.Handler
import android.widget.Toast
import androidx.core.net.toUri
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

class TrainTimeTable {

    //時刻表を入れる配列
    val trainList = arrayListOf<String>()
    //時間が入る。
    val trainHourList = arrayListOf<Int>()

    //上り
    var upNextTime = ""
    //特急か区間急行とか
    var upTrainType = ""
    //行き先（高尾山口　など）
    var upTrainFor = ""

    //下り
    var downNextTime = ""
    //特急か区間急行とか
    var downTrainType = ""
    //行き先（高尾山口　など）
    var downTrainFor = ""

    //駅名
    var stationName = ""


    //時刻表の時間別の配列が入った配列
    //例：[["10","20"],["10","20"]]
    val upTrainTimeTableListList = arrayListOf<ArrayList<String>>()
    //時刻表の時間別の配列が入った配列の中の電車の種類（特急・区間急行）等の配列
    //上の時刻表と同じ構造です。
    //例：[["特","区"],["特"]]
    val upTrainTimeTableListTypeList = arrayListOf<ArrayList<String>>()
    //時刻表の時間別の配列が入った配列の中の電車の行き先（高尾山口）等の配列
    //上の時刻表と同じ構造です。
    //例：[["山"],[""]]
    val upTrainTimeTableListForList = arrayListOf<ArrayList<String>>()
    //時刻表の時間別の配列が入った配列の中の時間を入れるだけの配列
    //例：["4","5"]
    val upTrainTimeTableListHourList = arrayListOf<String>()

    //それから下りVerも用意する
    val downTrainTimeTableListList = arrayListOf<ArrayList<String>>()
    val downTrainTimeTableListTypeList = arrayListOf<ArrayList<String>>()
    val downTrainTimeTableListForList = arrayListOf<ArrayList<String>>()
    val downTrainTimeTableListHourList = arrayListOf<String>()

    //次の時間の時刻表が必要？
    //例：18時にはもう電車ないので次の19時に
    var isNextTimeTable = false


    /**
     * 時刻表をスクレイピングする
     * これはUIスレッドで呼ばないで下さい。インターネット通信を行うので。
     * */
    fun getTrainTimeTable(context: Context, url: String) {
        //UIスレッド
        val document = Jsoup.connect(url)
            .get()
        //取り出す
        val tr = document.getElementsByTag("tr")
        tr.forEach {
            //hh_5からhh_24まで取る
            for (i in 5..24) {
                if (it.id() == "hh_${i}") {
                    val li = it.getElementsByClass("timeNumb")
                    li.forEach {
                        trainList.add(it.text())
                        trainHourList.add(i)
                    }
                    //特急とか
                    val trainFor = it.getElementsByClass("trainFor")
                    trainFor.forEach {
                        //println(it.text())
                    }
                    val trainType = it.getElementsByClass("trainType")
                    trainType.forEach {
                        //println(it.text())
                    }
                    //println("すくれいぴんぐ")
                }
            }
        }
    }

    /**
     * 次の電車を出す
     * これはインターネットを利用するためUIスレッドで呼ばないで下さい。
     * */
    fun getNextTrain(upURL: String, downURL: String) {
        getNextTrain(upURL, true)
        getNextTrain(downURL, false)
    }

    //boolean -> true 上り
    //boolean -> false 下り
    fun getNextTrain(url: String, boolean: Boolean) {
        //時刻
        var nextTrainTime = ""
        //大小比較のために
        var timeTemp = 100

        //一時的に特急・区間急行 / 行き先を保存しておく
        var tmpType = ""
        var tmpFor = ""

        val date = Calendar.getInstance()
        //平日・土曜・日曜に対応させる
        //祝日は無理だな
        val dayOfWeek = date.get(Calendar.DAY_OF_WEEK)
        var parameter = "1"
        when (dayOfWeek) {
            Calendar.SATURDAY -> {
                //土曜
                parameter = "2"
            }
            Calendar.SUNDAY -> {
                //日曜
                parameter = "4"
            }
        }

        //今の時間を出す
        var hour = date.get(Calendar.HOUR_OF_DAY)
        var minute = date.get(Calendar.MINUTE)
        //  hour = 18
        //  minute = 58

        //URLつなげる
        val uri = url.toUri().buildUpon()
        uri.appendQueryParameter("kind", parameter)

        //UIスレッド
        val document = Jsoup.connect(uri.build().toString())
            .get()

        //取り出す
        val tr = document.getElementsByTag("tr")
        tr.forEach {
            val trForeach = it
            var id = "hh_${hour}"
            //0->24
            if (hour == 0) {
                id = "hh_24"
            }
            //時間で絞る
            if (it.id() == id) {
                //次に来る電車を求める
                val li = it.getElementsByClass("timeNumb")
                li.forEach {
                    //時間
                    var trainTime = it.getElementsByTag("dt")[0].text()
                    // 特急・区間急行など
                    val trainType = it.getElementsByClass("trainType")
                    //行き先？
                    val trainFor = it.getElementsByClass("trainFor")

                    //正規表現で数字だけ出す（もしかしたら数字以外も入る可能性）
                    val pattern = Pattern.compile("[0-9０-９]+")
                    val matcher = pattern.matcher(trainTime)
                    if (matcher.find()) {
                        //数字だけとった
                        trainTime = matcher.group()

                        //引き算して最も低い値が次の電車なのでは
                        val tmp = (trainTime.toInt()) - minute
                        //値が負の値になってる場合は手遅れ
                        if (0 <= tmp) {
                            if (timeTemp > tmp) {
                                timeTemp = tmp
                                //一桁は先頭に0を入れる
                                if (trainTime.length == 1) {
                                    trainTime = "0$trainTime"
                                }
                                nextTrainTime = "${hour}:$trainTime"
                                tmpFor = trainFor.text()
                                tmpType = trainType.text()
                            }
                        }
                    }
                }
            }
        }
        if (nextTrainTime.isEmpty()) {
            isNextTimeTable = true
            //この時間にはない
            //7:55 -> 8:00
            timeTemp = 100
            //時間を足す
            val id = "hh_${hour + 1}"
            val tr = document.getElementsByTag("tr")
            tr.forEach {
                if (it.id() == id) {
                    //次に来る電車を求める
                    val li = it.getElementsByClass("timeNumb")
                    li.forEach {
                        //時間
                        var trainTime = it.getElementsByTag("dt")[0].text()
                        // 特急・区間急行など
                        val trainType = it.getElementsByClass("trainType")
                        //行き先？
                        val trainFor = it.getElementsByClass("trainFor")
                        //正規表現で数字だけ出す（もしかしたら数字以外も入る可能性）
                        val pattern = Pattern.compile("[0-9０-９]+")
                        val matcher = pattern.matcher(trainTime)
                        if (matcher.find()) {
                            //数字だけとった
                            trainTime = matcher.group()
                            ///次の時間の一番最初の電車
                            val tmp = (trainTime.toInt())
                            //値が負の値になってる場合は手遅れ
                            if (0 <= tmp) {
                                if (timeTemp > tmp) {
                                    timeTemp = tmp
                                    //一桁は先頭に0を入れる
                                    if (trainTime.length == 1) {
                                        trainTime = "0$trainTime"
                                    }
                                    nextTrainTime = "${hour + 1}:$trainTime"
                                    tmpFor = trainFor.text()
                                    tmpType = trainType.text()
                                }
                            }
                        }
                    }
                }
            }
        }
        //配列作る
        //縦の列
        // ・ ↓
        // ・ ↓
        // ・ ↓
        val table = document.getElementsByTag("tr")
        table.forEach {
            //一つぶん
            //横
            // ・→・→・
            val item = it.getElementsByClass("timeNumb")
            //時間
            var hour = ""
            if (it.getElementsByClass("hour").text() != "時") {
                hour = it.getElementsByClass("hour").text()
            }
            //println(hour)
            //それぞれ配列作る
            val trainTimeList = arrayListOf<String>()
            val trainTypeList = arrayListOf<String>()
            val trainForList = arrayListOf<String>()
            item.forEach {
                //時間
                var time = it.getElementsByTag("dt")[0].text()
                //特急・区間・各駅とか
                val type = it.getElementsByClass("trainType").text()
                //行き先
                val trainFor = it.getElementsByClass("trainFor").text()

                //正規表現で数字だけ出す（もしかしたら数字以外も入る可能性）
                val pattern = Pattern.compile("[0-9０-９]+")
                val matcher = pattern.matcher(time)
                if (matcher.find()) {
                    //数字だけとった
                    time = matcher.group()
                }

                //時間の分のところ、一桁のときは先頭に0をつける
                if (time.length == 1) {
                    time = "0$time"
                }

                trainTimeList.add(time)
                trainTypeList.add(type)
                trainForList.add(trainFor)

            }
            if (boolean) {
                //上り
                upTrainTimeTableListList.add(trainTimeList)
                upTrainTimeTableListTypeList.add(trainTypeList)
                upTrainTimeTableListForList.add(trainForList)
                upTrainTimeTableListHourList.add(hour)
            } else {
                //下り
                downTrainTimeTableListList.add(trainTimeList)
                downTrainTimeTableListTypeList.add(trainTypeList)
                downTrainTimeTableListForList.add(trainForList)
                downTrainTimeTableListHourList.add(hour)
            }
        }

        if (boolean) {
            //次の電車
            //時間の分のところ、一桁のときは先頭に0をつける
            if (nextTrainTime.length == 1) {
                nextTrainTime = "0$nextTrainTime"
            }
            upNextTime = nextTrainTime
            upTrainType = tmpType
            upTrainFor = tmpFor
        } else {
            //次の電車
            //時間の分のところ、一桁のときは先頭に0をつける
            if (nextTrainTime.length == 1) {
                nextTrainTime = "0$nextTrainTime"
            }
            downNextTime = nextTrainTime
            downTrainType = tmpType
            downTrainFor = tmpFor
        }

        //名前
        stationName = document.getElementsByClass("title")[1].text()
    }

}