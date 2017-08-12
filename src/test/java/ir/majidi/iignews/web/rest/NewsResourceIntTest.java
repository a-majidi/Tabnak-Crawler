package ir.majidi.iignews.web.rest;

import ir.majidi.iignews.MajidiIigApp;

import ir.majidi.iignews.domain.News;
import ir.majidi.iignews.repository.NewsRepository;
import ir.majidi.iignews.service.NewsService;
import ir.majidi.iignews.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the NewsResource REST controller.
 *
 * @see NewsResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = MajidiIigApp.class)
public class NewsResourceIntTest {

    private static final Integer DEFAULT_NEWS_ID = 1;
    private static final Integer UPDATED_NEWS_ID = 2;

    private static final String DEFAULT_NEWS_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_NEWS_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_NEWS_BODY = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_BODY = "BBBBBBBBBB";

    private static final String DEFAULT_NEWS_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_NEWS_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_NEWS_URL = "AAAAAAAAAA";
    private static final String UPDATED_NEWS_URL = "BBBBBBBBBB";

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restNewsMockMvc;

    private News news;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        NewsResource newsResource = new NewsResource(newsService);
        this.restNewsMockMvc = MockMvcBuilders.standaloneSetup(newsResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static News createEntity(EntityManager em) {
        News news = new News()
            .newsID(DEFAULT_NEWS_ID)
            .newsTitle(DEFAULT_NEWS_TITLE)
            .newsDescription(DEFAULT_NEWS_DESCRIPTION)
            .newsBody(DEFAULT_NEWS_BODY)
            .newsKeywords(DEFAULT_NEWS_KEYWORDS)
            .newsCategory(DEFAULT_NEWS_CATEGORY)
            .newsUrl(DEFAULT_NEWS_URL);
        return news;
    }

    @Before
    public void initTest() {
        news = createEntity(em);
    }

    @Test
    @Transactional
    public void createNews() throws Exception {
        int databaseSizeBeforeCreate = newsRepository.findAll().size();

        // Create the News
        restNewsMockMvc.perform(post("/api/news")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(news)))
            .andExpect(status().isCreated());

        // Validate the News in the database
        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeCreate + 1);
        News testNews = newsList.get(newsList.size() - 1);
        assertThat(testNews.getNewsID()).isEqualTo(DEFAULT_NEWS_ID);
        assertThat(testNews.getNewsTitle()).isEqualTo(DEFAULT_NEWS_TITLE);
        assertThat(testNews.getNewsDescription()).isEqualTo(DEFAULT_NEWS_DESCRIPTION);
        assertThat(testNews.getNewsBody()).isEqualTo(DEFAULT_NEWS_BODY);
        assertThat(testNews.getNewsKeywords()).isEqualTo(DEFAULT_NEWS_KEYWORDS);
        assertThat(testNews.getNewsCategory()).isEqualTo(DEFAULT_NEWS_CATEGORY);
        assertThat(testNews.getNewsUrl()).isEqualTo(DEFAULT_NEWS_URL);
    }

    @Test
    @Transactional
    public void createNewsWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = newsRepository.findAll().size();

        // Create the News with an existing ID
        news.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restNewsMockMvc.perform(post("/api/news")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(news)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllNews() throws Exception {
        // Initialize the database
        newsRepository.saveAndFlush(news);

        // Get all the newsList
        restNewsMockMvc.perform(get("/api/news?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(news.getId().intValue())))
            .andExpect(jsonPath("$.[*].newsID").value(hasItem(DEFAULT_NEWS_ID)))
            .andExpect(jsonPath("$.[*].newsTitle").value(hasItem(DEFAULT_NEWS_TITLE.toString())))
            .andExpect(jsonPath("$.[*].newsDescription").value(hasItem(DEFAULT_NEWS_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].newsBody").value(hasItem(DEFAULT_NEWS_BODY.toString())))
            .andExpect(jsonPath("$.[*].newsKeywords").value(hasItem(DEFAULT_NEWS_KEYWORDS.toString())))
            .andExpect(jsonPath("$.[*].newsCategory").value(hasItem(DEFAULT_NEWS_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].newsUrl").value(hasItem(DEFAULT_NEWS_URL.toString())));
    }

    @Test
    @Transactional
    public void getNews() throws Exception {
        // Initialize the database
        newsRepository.saveAndFlush(news);

        // Get the news
        restNewsMockMvc.perform(get("/api/news/{id}", news.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(news.getId().intValue()))
            .andExpect(jsonPath("$.newsID").value(DEFAULT_NEWS_ID))
            .andExpect(jsonPath("$.newsTitle").value(DEFAULT_NEWS_TITLE.toString()))
            .andExpect(jsonPath("$.newsDescription").value(DEFAULT_NEWS_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.newsBody").value(DEFAULT_NEWS_BODY.toString()))
            .andExpect(jsonPath("$.newsKeywords").value(DEFAULT_NEWS_KEYWORDS.toString()))
            .andExpect(jsonPath("$.newsCategory").value(DEFAULT_NEWS_CATEGORY.toString()))
            .andExpect(jsonPath("$.newsUrl").value(DEFAULT_NEWS_URL.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingNews() throws Exception {
        // Get the news
        restNewsMockMvc.perform(get("/api/news/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateNews() throws Exception {
        // Initialize the database
        newsService.save(news);

        int databaseSizeBeforeUpdate = newsRepository.findAll().size();

        // Update the news
        News updatedNews = newsRepository.findOne(news.getId());
        updatedNews
            .newsID(UPDATED_NEWS_ID)
            .newsTitle(UPDATED_NEWS_TITLE)
            .newsDescription(UPDATED_NEWS_DESCRIPTION)
            .newsBody(UPDATED_NEWS_BODY)
            .newsKeywords(UPDATED_NEWS_KEYWORDS)
            .newsCategory(UPDATED_NEWS_CATEGORY)
            .newsUrl(UPDATED_NEWS_URL);

        restNewsMockMvc.perform(put("/api/news")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedNews)))
            .andExpect(status().isOk());

        // Validate the News in the database
        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate);
        News testNews = newsList.get(newsList.size() - 1);
        assertThat(testNews.getNewsID()).isEqualTo(UPDATED_NEWS_ID);
        assertThat(testNews.getNewsTitle()).isEqualTo(UPDATED_NEWS_TITLE);
        assertThat(testNews.getNewsDescription()).isEqualTo(UPDATED_NEWS_DESCRIPTION);
        assertThat(testNews.getNewsBody()).isEqualTo(UPDATED_NEWS_BODY);
        assertThat(testNews.getNewsKeywords()).isEqualTo(UPDATED_NEWS_KEYWORDS);
        assertThat(testNews.getNewsCategory()).isEqualTo(UPDATED_NEWS_CATEGORY);
        assertThat(testNews.getNewsUrl()).isEqualTo(UPDATED_NEWS_URL);
    }

    @Test
    @Transactional
    public void updateNonExistingNews() throws Exception {
        int databaseSizeBeforeUpdate = newsRepository.findAll().size();

        // Create the News

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restNewsMockMvc.perform(put("/api/news")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(news)))
            .andExpect(status().isCreated());

        // Validate the News in the database
        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteNews() throws Exception {
        // Initialize the database
        newsService.save(news);

        int databaseSizeBeforeDelete = newsRepository.findAll().size();

        // Get the news
        restNewsMockMvc.perform(delete("/api/news/{id}", news.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<News> newsList = newsRepository.findAll();
        assertThat(newsList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(News.class);
        News news1 = new News();
        news1.setId(1L);
        News news2 = new News();
        news2.setId(news1.getId());
        assertThat(news1).isEqualTo(news2);
        news2.setId(2L);
        assertThat(news1).isNotEqualTo(news2);
        news1.setId(null);
        assertThat(news1).isNotEqualTo(news2);
    }
}
