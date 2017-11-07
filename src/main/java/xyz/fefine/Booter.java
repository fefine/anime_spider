package xyz.fefine;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import xyz.fefine.spider.BanGuMiPageProcessor;

/**
 * Created by feng_sh on 17-11-5.
 */
public class Booter {
    public static void main(String[] args) {

        Spider.create(new BanGuMiPageProcessor())
                .addUrl("http://bangumi.tv/anime/browser?page=1")
//                .addPipeline(new JsonFilePipeline("/home/wkzq/blog.json"))
                .thread(1)
                .run();
    }
}
