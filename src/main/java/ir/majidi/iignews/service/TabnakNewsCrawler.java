package ir.majidi.iignews.service;

import ir.majidi.iignews.domain.News;
import jdk.internal.dynalink.beans.StaticClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
/**
 * Created by abbas on 8/12/2017.
 */
@Component
public class TabnakNewsCrawler {
    private final Logger log = LoggerFactory.getLogger(TabnakNewsCrawler.class);

    private final NewsService newsService;

    private HashSet<String> links;
    private List<List<String>> articles;

    public static int lastId=0;
    public static boolean isFirstTime = true;

    public TabnakNewsCrawler(NewsService newsService) {
        this.newsService = newsService;

        links = new HashSet<>();
        articles = new ArrayList<>();



    }


    @Scheduled(fixedRate = 60000)
    public void scheduleFixedRateTask() {


        log.debug("Timmer starts right now. fetching data from TABNAK.ir : {}", "");

//        News news = new News();
//        news.setNewsBody("<html> hello world</html>"+(int)Math.random()*100);
//        news.setNewsCategory("salam, hello, category"+(int)Math.random()*100);
//        news.setNewsDescription(" Description");
//        news.setNewsID((int)Math.random()*100);
//        news.setNewsTitle("عنوان");
//
//        newsService.save(news);


        getPageLinks("http://www.tabnak.ir/fa");
        getArticles();

        isFirstTime = false;
    }




    //Find all URLs that start with "http://www.tabnak.ir/" and add them to the HashSet
    public void getPageLinks(String URL) {
//        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).get();
                Elements otherLinks = document.select("a[href^=\"/fa/news/\"]");

                for (Element page : otherLinks) {

                    String classname = page.attr("class");
                    if("title_main".equals(classname) || "title5".equals(classname)){
                        if (!links.contains("http://www.tabnak.ir"+page.attr("href"))) {
                            links.add("http://www.tabnak.ir"+page.attr("href"));
                            System.out.println("http://www.tabnak.ir"+page.attr("href"));
                        }

                    }


                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
//        }
    }

    //Connect to each link saved in the article and find all the articles in the page
    public void getArticles() {
        links.forEach(x -> {
            Document document;
            try {


                int firstIndex = x.indexOf("/news/")+6;
                String tmp = x.substring(firstIndex,x.indexOf("/",firstIndex));

                int newsid = Integer.valueOf(x.substring(firstIndex,x.indexOf("/",firstIndex)));

//                if(isFirstTime || newsid>lastId)
//                {

                    lastId=newsid;

                    News news = new News();

                    news.setNewsUrl(x);
                    news.setNewsID(newsid);

                document = Jsoup.connect(x).get();//<a href="/fa/news/
                Elements title = document.select("h1 a[href^=\"/fa/news/\"]");//titles //remove
                Elements category = document.select("div a[href^=\"/fa/archive?service_id=\"]");  //categories
                Elements tags = document.select("div a[href^=\"/fa/tag/1/\"]");
                Elements description = document.select("meta");
                Elements body = document.select("div");

                String newsurl = x;


                news.setNewsUrl(newsurl);


                //extract title from string
                String titleExtract = title.text();

                news.setNewsTitle(titleExtract);

                StringBuffer categoryList = new StringBuffer();
                for (Element article : category) {

                    String categoryExtract = article.text();
                    if(!"".equals(categoryExtract))
                        categoryList.append(categoryExtract+" - ");

                }
                news.setNewsCategory(categoryList.toString());


                StringBuffer tagsList = new StringBuffer();
                for (Element article : tags) {

                    String tagsExtract = article.text();

                    if(!"".equals(tagsExtract))
                        tagsList.append(tagsExtract+" - ");

                }
                news.setNewsKeywords(tagsList.toString());


//                StringBuffer descriptionList = new StringBuffer();
                for (Element article : description) {

                    String descriptionExtract="";
                    if(article.attr("name").equals("description")){
                        descriptionExtract = article.attr("content");
                    }
                    //for db . max db size for varchar in oracle is 4000 so i will just 4000 character in db.
                    if(descriptionExtract.length()>4000)descriptionExtract= descriptionExtract.substring(0,4000);
                    if(!descriptionExtract.equals("")) {
//                        descriptionList.append(description+" - ");
                        news.setNewsDescription(descriptionExtract);
                    }
                }
//                news.setNewsDescription(descriptionList.toString());


                for (Element article : body) {

                    String bodyExtract="";
                    if(article.attr("class").equals("body")){
                        bodyExtract =  article.html();
                        //for db . max db size for varchar in oracle is 4000 so i will just 4000 character in db.
                        if(bodyExtract.length()>4000)bodyExtract= bodyExtract.substring(0,4000);
                    }
                    if(!bodyExtract.equals("")) {
                        news.setNewsBody(bodyExtract);
                    }

                }
                    newsService.save(news);

//                }//end of if


            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }


        );
    }
}
