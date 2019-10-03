package io.github.takusan23.nexttrain.Scraping

import android.content.Context
import android.os.Handler
import android.widget.Toast
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import java.util.*
import java.util.regex.Pattern

class TrainTimeTable {

    //時刻表を入れる配列
    val trainList = arrayListOf<String>()
    //時間が入る。
    val trainHourList = arrayListOf<Int>()

    //上り
    var upNextTime = ""
    //特急か区間急行とか
    var upTrainType = ""

    //下り
    var downNextTime = ""
    var downTrainType = ""

    //駅名
    var stationName = ""

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
                    println("すくれいぴんぐ")
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
    private fun getNextTrain(url: String, boolean: Boolean) {
        //時刻
        var nextTrainTime = ""
        //大小比較のために
        var timeTemp = 100

        var tmpType = ""

        //UIスレッド
        val document = Jsoup.connect(url)
            .get()
        //取り出す
        val tr = document.getElementsByTag("tr")
        tr.forEach {
            //今の時間を出す
            val date = Calendar.getInstance()
            val hour = date.get(Calendar.HOUR_OF_DAY)
            val minute = date.get(Calendar.MINUTE)
            //  val hour = 7
            //  val minute = 45
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
                    var trainTime = it.getElementsByTag("dt")[0].text()
                    //正規表現で数字だけ出す（もしかしたら数字以外も入る可能性）
                    val pattern = Pattern.compile("[0-9０-９]+")
                    val matcher = pattern.matcher(trainTime)
                    if (matcher.find()) {
                        //数字だけとった
                        trainTime = matcher.group()


/*
                        //この時間にはない
                        //7:55 -> 8:00
                        if ((trainTime.toInt()) < minute) {
                            //時間を足す
                            val id = "hh_${hour + 1}"
                            if (it.id() == id) {
                                //次に来る電車を求める
                                val li = it.getElementsByClass("timeNumb")
                                li.forEach {
                                    var trainTime = it.getElementsByTag("dt")[0].text()
                                    //正規表現で数字だけ出す（もしかしたら数字以外も入る可能性）
                                    val pattern = Pattern.compile("[0-9０-９]+")
                                    val matcher = pattern.matcher(trainTime)
                                    if (matcher.find()) {
                                        //数字だけとった
                                        trainTime = matcher.group()
                                        ///引き算して最も低い値が次の電車なのでは
                                        val tmp = (trainTime.toInt()) - minute
                                        //値が負の値になってる場合は手遅れ
                                        if (0 < tmp) {
                                            if (timeTemp > tmp) {
                                                timeTemp = tmp
                                                nextTrainTime = "${hour}:$trainTime"
                                            }
                                        }
                                    }
                                }
                            }


                        } else {

                        }
*/

                        //引き算して最も低い値が次の電車なのでは
                        val tmp = (trainTime.toInt()) - minute
                        //値が負の値になってる場合は手遅れ
                        if (0 < tmp) {
                            if (timeTemp > tmp) {
                                timeTemp = tmp
                                nextTrainTime = "${hour}:$trainTime"
                            }
                        }

                    }
                }
            }
        }

        if (boolean) {
            //次の電車
            upNextTime = nextTrainTime
        } else {
            //次の電車
            downNextTime = nextTrainTime
        }

        //名前
        stationName = document.getElementsByClass("title")[1].text()

    }

}