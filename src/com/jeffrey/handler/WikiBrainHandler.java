package com.jeffrey.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.wikibrain.conf.ConfigurationException;
import org.wikibrain.conf.Configurator;
import org.wikibrain.core.cmd.Env;
import org.wikibrain.core.cmd.EnvBuilder;
import org.wikibrain.core.dao.DaoException;
import org.wikibrain.core.dao.LocalLinkDao;
import org.wikibrain.core.dao.LocalPageDao;
import org.wikibrain.core.dao.UniversalPageDao;
import org.wikibrain.core.lang.Language;
import org.wikibrain.core.lang.LanguageSet;
import org.wikibrain.core.model.LocalLink;
import org.wikibrain.core.model.LocalPage;
import org.wikibrain.core.model.NameSpace;
import org.wikibrain.core.model.Title;
import org.wikibrain.core.model.UniversalPage;

public class WikiBrainHandler {
	private static WikiBrainHandler instance;
	private Env env;
	private Configurator conf;
	private LocalPageDao lpDao;
	private LocalLinkDao llDao;
	private UniversalPageDao upDao;
	
	private LanguageSet langs;
	
	public static final String FORMATCODE_JSON = "json";
	public static final String FORMATCODE_PLAINTEXT = "plaintext";
	
	{
		try {
			env = new EnvBuilder().build();
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conf = env.getConfigurator();
		try {
			lpDao = conf.get(LocalPageDao.class);
			llDao = conf.get(LocalLinkDao.class, "sql");
			upDao = conf.get(UniversalPageDao.class);
		} catch (ConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private WikiBrainHandler() {
		langs = new LanguageSet("simple,la");
	}
	
	public static WikiBrainHandler getInstance() {
		if(instance == null) {
			instance = new WikiBrainHandler();
		}
		return instance;
	}
	
	public void setLangs(LanguageSet langs) {
		this.langs = langs;
	}
	
	public LocalPage fetchRandom() throws DaoException {
		Random rand = new Random();
		int id = 1 + rand.nextInt(455242);
		LocalPage page = getLocalPageByID(id, Language.SIMPLE);
		if(page != null && page.getNameSpace() == NameSpace.ARTICLE) {
			return page; 
		} else {
			return fetchRandom();
		}
	}
	
	public LocalPage getLocalPageByID(int id, Language lang) throws DaoException {
		return lpDao.getById(lang, id);
	}
	
	public LocalPage getLocalPageByTitle(String title, Language lang) throws DaoException {
		return lpDao.getByTitle(new Title(title, lang), NameSpace.ARTICLE);
	}
	
	public LocalPage getLocalPageByTitle(String title, 
										  Language nativeLang, 
										  Language targetLang) throws DaoException {
		LocalPage nativePage = lpDao.getByTitle(new Title(title, nativeLang), NameSpace.ARTICLE);
		UniversalPage concept = upDao.getByLocalPage(nativePage);
		return lpDao.getById(targetLang, concept.getLocalId(targetLang));
	}
	
	public boolean hasArticle(Language nativeLang, Language targetLang, String title) {
        UniversalPage page;
        try {
            LocalPage lp = getLocalPageByTitle(title, nativeLang);
            if(lp != null) {
                page = upDao.getByLocalPage(getLocalPageByTitle(title, nativeLang));
                if(page.getLocalId(targetLang) != -1) {
                    return true;
                }
            }
        } catch(DaoException e) {
            return false;
        }
		return false;
	}
	
	
	//http://af.wikipedia.org/wiki/Appel
	public String getURL(LocalPage page) throws DaoException {
		String[] urlElements = page.getCompactUrl().split("/");
		return "http://" + (urlElements[2].equals("simple") ? "en" : urlElements[2]) + ".wikipedia.org/wiki/" + urlElements[4];
	}
	
	public List<ArticleDataObject> getLanguagesWithArticle(Language nativeLang, String title) throws DaoException {
		List<ArticleDataObject> langsWithArticle = new ArrayList<ArticleDataObject>();
		
		for (Language lang : langs) {
            if(hasArticle(nativeLang, lang, title)) {
            	LocalPage lp = getLocalPageByTitle(title, nativeLang, lang);
				langsWithArticle.add(new ArticleDataObject(lp.getTitle().getTitleStringWithoutNamespace(), lang, getURL(lp)));
			}
		}
		
		return langsWithArticle;
	}
	
	public String getLanguagesWithArticle(Language nativeLang, 
										  String title, 
										  //String formatCode,
										  boolean includeURL) throws DaoException {
		List<String> langsWithArticle = new ArrayList<String>();
		
		for (Language lang : langs) {
            if(hasArticle(nativeLang, lang, title)) {
				langsWithArticle.add(lang.toString() + " " +
						getURL(getLocalPageByTitle(title, nativeLang, lang)));
			}
		}
		
//		switch (formatCode) {
//		case FORMATCODE_JSON:
//			try {
//				return new JSONObject().put("languages", langsWithArticle).toString();
//			} catch (JSONException e) {
//				e.printStackTrace();
//				return "(None)";
//			}
//			break;
//		}
		
		
		return langsWithArticle.size() == 0 ? "(None)" : langsWithArticle.toString();
	}
	
	public List<LocalLink> getLinks(LocalPage page, boolean outlinks) throws DaoException {
		List<LocalLink> links = new ArrayList<LocalLink>();
		for(LocalLink link : llDao.getLinks(page.getLanguage(), page.getLocalId(), outlinks)) {
			if(link.isParseable() && link.getDestId() != -1)
				links.add(link);
		}
		return links;
	}
	
	public LanguageSet getLangs() {
		return langs;
	}
}