package io.bettergram.service.api;

import android.annotation.SuppressLint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.bettergram.messenger.R;
import io.bettergram.telegram.messenger.LocaleController;
import io.bettergram.utils.HttpDate;
import io.bettergram.utils.Time;
import io.bettergram.utils.io.IOUtils;

public class NewsApi {

    //@formatter:off
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat FROM_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:SS'Z'");
    //@SuppressLint("SimpleDateFormat")
    //public static final SimpleDateFormat FROM_FORMAT2 = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
    //@formatter:on
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat TO_FORMAT = new SimpleDateFormat("MMM dd");

    public static final String LIVE_COIN_WATCH_NEWS_URL = "https://api.bettergram.io/v1/news";

    /**
     * Gets news related to cryptocurrency
     */
    public static String getNewsQuietly() throws IOException, JSONException {
        URL newsURL = new URL(LIVE_COIN_WATCH_NEWS_URL);
        JSONObject jsonData = new JSONObject(
                IOUtils.toString(
                        newsURL,
                        Charset.forName("UTF-8")
                )
        );

        return jsonData.toString();
    }

    /**
     * Gets formatted date
     */
    public static String formatDate(String unformattedDate) {
        try {
            //Date date = FROM_FORMAT2.parse(unformattedDate);
            Date date = HttpDate.parse(unformattedDate);
            return TO_FORMAT.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatToYesterdayOrToday(String date) {
        try {
            //Date dateTime = FROM_FORMAT2.parse(date);
            Date dateTime = HttpDate.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return Time.getTimeAgo(dateTime);
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return LocaleController.getString("NewsYesterday", R.string.NewsYesterday);
            } else {
                return formatDate(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
