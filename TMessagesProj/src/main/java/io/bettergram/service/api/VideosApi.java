package io.bettergram.service.api;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.bettergram.messenger.R;
import io.bettergram.telegram.messenger.LocaleController;
import io.bettergram.utils.Counter;
import io.bettergram.utils.io.IOUtils;

public class VideosApi {

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat DURATION_FORMAT = new SimpleDateFormat("mm:ss");
    //@formatter:off
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FROM_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    @SuppressLint("SimpleDateFormat")
    public static final SimpleDateFormat FROM_FORMAT2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    //@formatter:on
    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat TO_FORMAT = new SimpleDateFormat("MMM dd");

    private static final String YOUTUBE_STATISTICS_URL = "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=%s&key=%s";

    public static final String LIVE_COIN_WATCH_VIDEO_URL = "https://api.bettergram.io/v1/videos";

    /**
     * Queries data for specific youtube video
     */
    @Nullable
    public static Map<String, String> getDataQuietly(@NonNull String videoId, String apiKey)
            throws IOException, JSONException, ParseException {
        URL statsURL = new URL(String.format(YOUTUBE_STATISTICS_URL, videoId, apiKey));
        JSONObject jsonData = new JSONObject(
                IOUtils.toString(
                        statsURL,
                        Charset.forName("UTF-8")
                )
        );

        Log.i(VideosApi.class.getName(), "json: " + jsonData.toString());

        Map<String, String> data = new HashMap<>();

        JSONArray itemsArray = jsonData.getJSONArray("items");
        JSONObject item = itemsArray.getJSONObject(0);
        if (item != null) {

            data.put("id", item.getString("id"));

            JSONObject snippet = item.getJSONObject("snippet");
            if (snippet != null) {

                String publishedAt = snippet.getString("publishedAt");
                Date date = FROM_FORMAT.parse(publishedAt);
                publishedAt = TO_FORMAT.format(date);

                data.put("publishedAt", publishedAt);
                data.put("title", snippet.getString("title"));
                data.put("channelTitle", snippet.getString("channelTitle"));
            }

            JSONObject contentDetails = item.getJSONObject("contentDetails");
            if (contentDetails != null) {
                String duration = contentDetails.getString("duration");
                data.put("duration", formatDuration(duration));
            }

            JSONObject statistics = item.getJSONObject("statistics");
            if (statistics != null) {
                long viewCount = Long.valueOf(statistics.getString("viewCount"));
                data.put("viewCount", Counter.format(viewCount));
            }
        }
        return data;
    }

    /**
     * Gets Youtube RSS
     */
    public static String getYoutubeRSSFeed() throws IOException, JSONException {
        URL statsURL = new URL(LIVE_COIN_WATCH_VIDEO_URL);
        JSONObject jsonData = new JSONObject(
                IOUtils.toString(
                        statsURL,
                        Charset.forName("UTF-8")
                )
        );
        return jsonData.toString();
    }

    /**
     * Formats youtube video duration
     */
    @SuppressLint("DefaultLocale")
    private static String formatDuration(String time) {
        time = time.substring(2);
        long duration = 0L;
        Object[][] indexes = new Object[][]{{"H", 3600}, {"M", 60}, {"S", 1}};
        for (int i = 0; i < indexes.length; i++) {
            int index = time.indexOf((String) indexes[i][0]);
            if (index != -1) {
                String value = time.substring(0, index);
                duration += Integer.parseInt(value) * (int) indexes[i][1] * 1000;
                time = time.substring(value.length() + 1);
            }
        }
        Date date = new Date(duration);
        return DURATION_FORMAT.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDate(String publishedAt) {
        try {
            Date date = FROM_FORMAT2.parse(publishedAt);
            return TO_FORMAT.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatToYesterdayOrToday(String date) {
        try {
            Date dateTime = FROM_FORMAT2.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateTime);
            Calendar today = Calendar.getInstance();
            Calendar yesterday = Calendar.getInstance();
            yesterday.add(Calendar.DATE, -1);

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)) {
                return LocaleController.getString("VideoToday", R.string.VideoToday);
            } else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR)) {
                return LocaleController.getString("VideoYesterday", R.string.VideoYesterday);
            } else {
                return formatDate(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
