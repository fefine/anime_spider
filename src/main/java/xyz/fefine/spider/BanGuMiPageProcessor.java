package xyz.fefine.spider;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by feng_sh on 17-11-5.
 */
public class BanGuMiPageProcessor implements PageProcessor {

    private Site site = Site.me()
            .setTimeOut(6000)
            .setRetryTimes(3)
            .setRetrySleepTime(1000)
            .setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36")
            .setDomain("bangumi.tv");

    private final Pattern PAGE_LIST_PATTERN = Pattern.compile("http://bangumi\\.tv/anime/browser\\?page=\\d+");
    private final String PAGE_LIST = ".*\\?page=[0-9]+";
    private final String ANIME_PATTERN = ".*/subject/[0-9]+";

    private final String ID_XPATH = "http://bangumi.tv/subject/(\\w+)";
    private final String COVER_XPATH = "//div[@class='infobox']//a";
    private final String TITLE_XPATH = "//div[@id='headerSubject']/h1[@class='nameSingle']/allText()";
    private final String INFO_XPATH = "//ul[@id='infobox']/li/allText()";
    private final String SUMMARY_XPATH = "//div[@id='subject_summary']/tidyText()";
    private final String SCORE_XPATH = "//div[@class='global_score']/span[@class='number']/text()";
    private final String TAG_NAME_XPATH = "//div[@class='subject_tag_section']/div[@class='inner']/a/text()";
    private final String TAG_COUNT_XPATH = "//div[@class='subject_tag_section']/div[@class='inner']/small[@class='grey']/text()";

    @Override
    public void process(Page page) {
        String url = page.getRequest().getUrl();
        Html html = page.getHtml();
        if (PAGE_LIST_PATTERN.matcher(url).find()) {
            // 列表页
            // 获取详情页
            List<String> urls = html.xpath("//ul[@id='browserItemList']")
                    .links().regex(ANIME_PATTERN).all();
            page.addTargetRequests(urls);
            // 获取列表页
            urls = html.xpath("//div[@class='page_inner']").links().regex(PAGE_LIST).all();
            page.addTargetRequests(urls);
        } else {
            // 详情页
            page.putField("id", Integer.parseInt(page.getUrl().regex(ID_XPATH).get()));
            // 封面图链接
            page.putField("coverImgHref", html.xpath(COVER_XPATH).links().get());
            // 标题
            page.putField("title", html.xpath(TITLE_XPATH).toString());
            // 其他信息
            page.putField("info", parseInfo(html.xpath(INFO_XPATH).all()));
            // 简介
            page.putField("summary", html.xpath(SUMMARY_XPATH).get());
            // 评分
            page.putField("score", Double.parseDouble(html.xpath(SCORE_XPATH).toString()));
            // 标签
            List<String> tagNames = html.xpath(TAG_NAME_XPATH).all();
            List<String> tagCounts = html.xpath(TAG_COUNT_XPATH).regex("(\\d+)").all();
            page.putField("tag", parseTags(tagNames, tagCounts));
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    private Map<String, String> parseInfo(List<String> infoLines) {
        Map<String, String> infoMaps = new HashMap<>();
        for (String infoLine : infoLines) {
            String[] strings = infoLine.split(": ");
            String key = strings[0];
            String value = strings[1];
            infoMaps.put(key, value);
        }
        return infoMaps;
    }

    private Map<String, Integer> parseTags(List<String> tagNames, List<String> tagCounts) {
        Map<String, Integer> tags = new HashMap<>();
        for (int i = 0; i < tagNames.size() && i < tagCounts.size(); i++) {
            String tagName = tagNames.get(i);
            int tagCount = Integer.parseInt(tagCounts.get(i));
            tags.put(tagName, tagCount);
        }
        return tags;
    }

}
