package com.example.LAndZVideoPlayer.Vdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Liu
public class VideoData {
    private static List<VideoBean> sVideoList;
    static {
        List<JSONObject> jsonList = new ArrayList<>(); //处理JSON数组
        String str = "[{\"_id\":\"5e9830b0ce330a0248e89d86\",\"feedurl\":\"http://jzvd.nathen.cn/video/1137e480-170bac9c523-0007-1823-c86-de200.mp4\",\"nickname\":\"王火火\",\"description\":\"这是第一条Feed数据\",\"likecount\":10000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/f402a0e012b14d41ad07939746844c5e00005.jpg\"},{\"_id\":\"5e9833dec47d14020e85f416\",\"feedurl\":\"http://jzvd.nathen.cn/video/e0bd348-170bac9c3b8-0007-1823-c86-de200.mp4\",\"nickname\":\"LILILI\",\"description\":\"这是一条一起学猫叫的视频\",\"likecount\":120000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/8bd6d06878fc4676a62290cbe8b5511f00005.jpg\"},{\"_id\":\"5e9833e0c47d14020e85f418\",\"feedurl\":\"http://jzvd.nathen.cn/video/2f03c005-170bac9abac-0007-1823-c86-de200.mp4\",\"nickname\":\"新闻启示录\",\"description\":\"赶紧把这个转发给你们的女朋友吧，这才是对她们最负责的AI\",\"likecount\":45000000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/371ddcdf7bbe46b682913f3d3353192000005.jpg\"},{\"_id\":\"5e9833e0a21527020d426e91\",\"feedurl\":\"http://jzvd.nathen.cn/video/7bf938c-170bac9c18a-0007-1823-c86-de200.mp4\",\"nickname\":\"综艺大咖秀\",\"description\":\"男明星身高暴击\",\"likecount\":98777000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/dabe6ca3c71942fd926a86c8996d750f00005.jpg\"},{\"_id\":\"5e9833e1c47d14020e85f43a\",\"feedurl\":\"http://jzvd.nathen.cn/video/47788f38-170bac9ab8a-0007-1823-c86-de200.mp4\",\"nickname\":\"南翔不爱吃饭\",\"description\":\"挑战手抓饼的一百种吃法第七天\",\"likecount\":500000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/edac56544e2f43bb827bd0e819db381000005.jpg\"},{\"_id\":\"5e983406a21527020d426f1f\",\"feedurl\":\"http://jzvd.nathen.cn/video/2d6ffe8f-170bac9ab87-0007-1823-c86-de200.mp4\",\"nickname\":\"王者主播那些事儿\",\"description\":\"你有试过蔡文姬打野吗？\",\"likecount\":1000000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/531f1e488eb84b898ae9ca7f6ba758ed00005.jpg\"},{\"_id\":\"5e98340da21527020d426f43\",\"feedurl\":\"http://jzvd.nathen.cn/video/633e0ce-170bac9ab65-0007-1823-c86-de200.mp4\",\"nickname\":\"十秒学做菜\",\"description\":\"两款爱吃的三明治分享\",\"likecount\":1010102,\"avatar\":\"http://jzvd.nathen.cn/snapshot/ad0331e78393457d88ded2257d9e47c800005.jpg\"},{\"_id\":\"5e983415a21527020d426f7b\",\"feedurl\":\"http://jzvd.nathen.cn/video/2d6ffe8f-170bac9ab87-0007-1823-c86-de200.mp4\",\"nickname\":\"九零后老母亲\",\"description\":\"从孕期到产后，老公一直要我用这个勺子喝汤\",\"likecount\":94321,\"avatar\":\"http://jzvd.nathen.cn/snapshot/6ae53110f7fd470683587746f027698400005.jpg\"},{\"_id\":\"5e98341da21527020d426f97\",\"feedurl\":\"http://jzvd.nathen.cn/video/51f7552c-170bac98718-0007-1823-c86-de200.mp4\",\"nickname\":\"FPX电子竞技俱乐部\",\"description\":\"甲方的需求：F P X冠军皮肤的起源\",\"likecount\":1200000,\"avatar\":\"http://jzvd.nathen.cn/snapshot/ef384b95897b470c80a4aca4dd1112a500005.jpg\"},{\"_id\":\"5e983423a21527020d426fbb\",\"feedurl\":\"http://jzvd.nathen.cn/video/2a101070-170bad88892-0007-1823-c86-de200.mp4\",\"nickname\":\"抖音官方广告报名！\",\"description\":\"买它！买它！买它！\",\"likecount\":480,\"avatar\":\"http://jzvd.nathen.cn/snapshot/86a055d08b514c9ca1e76e76862105ec00005.jpg\"}]";
       /* HttpClient httpClient = new DefaultHttpClent();
        URL url = new URL(path);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url
                    .openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        conn.setReadTimeout(5000);
        int code = 0;
        try {
            code = conn.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (code == 200) {
            InputStream is = null;
            try {
                is = conn.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = StreamTools.ReadStream(is);

        String url = "http://192.168.17.81:8080/querybooks/QueryServlet";
        //  第1步：创建HttpGet对象
        HttpGet httpGet = new HttpGet(url);
        //  第2步：使用execute方法发送HTTP GET请求，并返回HttpResponse对象
        httpResponse = new DefaultHttpClient().execute(httpGet);
//        //  判断请求响应状态码，状态码为200表示服务端成功响应了客户端的请求
//        if (httpResponse.getStatusLine().getStatusCode() == 200)
//        {     //  第3步：使用getEntity方法获得返回结果
//            String result = EntityUtils.toString(httpResponse.getEntity());  //  去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格
//        }*/
        try {
            JSONArray messages = new JSONArray(str); // 首先把字符串转成 JSONArray对象
            for(int i=0;i < messages.length();i++)
                jsonList.add(messages.getJSONObject(i));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        sVideoList = new ArrayList<>();
        for(int i = 0; i<jsonList.size(); i++)
        {
            String videoUrls = null;
            String videoCovers = null;
            String videoTitles = null;
            String videoAuthor = null;
            Integer videoLikeCount = 0;

            try {
                videoUrls = jsonList.get(i).getString("feedurl");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                videoCovers = jsonList.get(i).getString("avatar");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                videoTitles = jsonList.get(i).getString("description");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                videoAuthor = jsonList.get(i).getString("nickname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                videoLikeCount = jsonList.get(i).getInt("likecount");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sVideoList.add(new VideoBean(videoUrls, videoCovers, videoTitles, videoAuthor, videoLikeCount));
        }
    }

    public static List<VideoBean> getVideoList() {

        return sVideoList;
    }

    public static VideoBean getVideo() {

        return sVideoList.get(0);
    }
}
