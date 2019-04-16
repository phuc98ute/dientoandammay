package com.concretepage.controller;

import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.concretepage.storage.StorageFileNotFoundException;
import com.concretepage.storage.StorageService;
import javax.validation.Valid;

import jbdc.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.concretepage.controller.UserInfoController;
import com.concretepage.entity.Article;
import com.concretepage.service.IUserInfoService;
import com.concretepage.storage.StorageFileNotFoundException;
import com.concretepage.storage.StorageService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import com.concretepage.controller.UserInfoController;

@Controller
@RequestMapping("app")
public class UserInfoController {
	@Autowired
	private IUserInfoService userInfoService;

	@GetMapping("login")
	public ModelAndView login() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("custom-login");
		return mav;
	}

	@GetMapping("secure/article-details")
	public ModelAndView getAllUserArticles() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userArticles", userInfoService.FindAll());
		mav.setViewName("articles");
		return mav;
	}

	@GetMapping("secure/login")
	public ModelAndView Login() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userArticles", userInfoService.FindAll());
		mav.setViewName("index-Admin");
		return mav;
	}
	@GetMapping("secure/cpanel")
	public ModelAndView cpanel() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("redirect:/app/secure/article-details");
		return mav;
	}

	@GetMapping("")
	public ModelAndView Home() {
		ModelAndView mav = new ModelAndView();
		mav.addObject("userArticles", userInfoService.FindAll());
		mav.setViewName("index");
		return mav;
	}

	@GetMapping("error")
	public ModelAndView error() {
		ModelAndView mav = new ModelAndView();
		String errorMessage = "You are not authorized for the requested data.";
		mav.addObject("errorMsg", errorMessage);
		mav.setViewName("403");
		return mav;

	}

	@GetMapping("Delete")
	public ModelAndView Delete(Model model, @RequestParam(value = "id", required = false, defaultValue = "") String id)
			throws ClassNotFoundException, SQLException {
		ModelAndView mav = new ModelAndView();
		System.out.print("id cua nguoi nay la" + id);
		/*
		 * PreparedStatement pr; java.sql.Connection
		 * conn=ConnectionUtils.getMyConnection(); pr=(PreparedStatement)
		 * conn.prepareStatement("DELETE FROM articles WHERE article_id="+id);
		 * pr.executeUpdate(); pr.close();
		 */
		userInfoService.Delete(userInfoService.FindOne(Integer.parseInt(id)));
		mav.setViewName("redirect:/app/secure/article-details");

		return mav;
	}

	@RequestMapping(value = "/app-insert", method = RequestMethod.GET)
	public ModelAndView app_insert(Model model,
			@RequestParam(value = "id", required = false, defaultValue = "") String id,
			@RequestParam(value = "title", required = false, defaultValue = "") String title,
			@RequestParam(value = "category", required = false, defaultValue = "") String category,
			@RequestParam(value = "conttent", required = false, defaultValue = "") String conttent,
			@RequestParam(value = "linktai", required = false, defaultValue = "") String linktai) throws Exception {
		ModelAndView mav = new ModelAndView();
		java.sql.Connection conn = ConnectionUtils.getMyConnection();
		PreparedStatement pr;
		pr = (PreparedStatement) conn.prepareStatement("INSERT INTO articles VALUES (?,?,?,?,?)");
		pr.setString(1, id);
		pr.setString(2, title);
		pr.setString(3, category);
		pr.setString(4, conttent);
		pr.setString(5,linktai);
		pr.executeUpdate();
		pr.close();
		model.addAttribute("files",
				storageService.loadAll()
						.map(path -> MvcUriComponentsBuilder
								.fromMethodName(UserInfoController.class, "serveFile", path.getFileName().toString())
								.build().toString())
						.collect(Collectors.toList()));

		mav.setViewName("redirect:/app/secure/article-details");
		return mav;
	}

	@GetMapping("insert")
	public ModelAndView insert() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("edit-news");
		return mav;
	}

	@RequestMapping(value = "/update")
	public ModelAndView update(Model model, @RequestParam(value = "id", required = false, defaultValue = "") String id)
			throws ClassNotFoundException, SQLException {
		ModelAndView mav = new ModelAndView();
		Article article = userInfoService.FindOne(Integer.parseInt(id));
		mav.addObject("article", article);
		mav.addObject("id", article.getArticleId());

		mav.addObject("title", article.getTitle());

		mav.addObject("category", article.getCategory());

		mav.addObject("conttent", article.getConttent());

		mav.setViewName("editnews");
		return mav;
	}

	@RequestMapping(value = "/detail")
	public ModelAndView Detail(Model model, @RequestParam(value = "id", required = false, defaultValue = "") String id)
			throws ClassNotFoundException, SQLException {
		ModelAndView mav = new ModelAndView();
		Article article = userInfoService.FindOne(Integer.parseInt(id));
		mav.addObject("article", article);

		mav.setViewName("Detail");
		return mav;
	}

	@PostMapping("/save")
	public String save(@Valid Article article) {

		userInfoService.Save(article);

		return "redirect:/app/secure/article-details";
	}

	// asdnasndnasnand,masnd,mna,msn,d
	/** Application name. */
	private static final String APPLICATION_NAME = "Drive API Java Quickstart";

	/** Directory to store user credentials for this application. */
	private static final java.io.File DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"),
			".credentials/drive-java-quickstart");

	/** Global instance of the {@link FileDataStoreFactory}. */
	private static FileDataStoreFactory DATA_STORE_FACTORY;

	/** Global instance of the JSON factory. */
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	/** Global instance of the HTTP transport. */
	private static HttpTransport HTTP_TRANSPORT;

	/**
	 * Global instance of the scopes required by this quickstart.
	 *
	 * If modifying these scopes, delete your previously saved credentials at
	 * ~/.credentials/drive-java-quickstart
	 */
	private static final List<String> SCOPES = Arrays.asList(DriveScopes.DRIVE);

	static {
		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
			DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
		} catch (Throwable t) {
			t.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Creates an authorized Credential object.
	 * 
	 * @return an authorized Credential object.
	 * @throws Exception
	 */
	public static Credential authorize() throws Exception {
		// Load client secrets.
		InputStream in = UserInfoController.class.getResourceAsStream("/client_secret.json");
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

		// Build flow and trigger user authorization request.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				clientSecrets, SCOPES).setDataStoreFactory(DATA_STORE_FACTORY).setAccessType("offline").build();
		Credential credential = new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
		System.out.println("Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
		return credential;
	}

	/**
	 * Build and return an authorized Drive client service.
	 * 
	 * @return an authorized Drive client service
	 * @throws Exception
	 */
	public static Drive getDriveService() throws Exception {
		Credential credential = authorize();
		return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();
	}

	private final StorageService storageService;

	@Autowired
	public UserInfoController(StorageService storageService) {
		this.storageService = storageService;
	}

	@GetMapping("/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

		Resource file = storageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping(value = "/uploadfile")
	public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes)
			throws Exception {

		storageService.store(file);

		Drive service = getDriveService();
		// ModelAndView mav = new ModelAndView();
		File fileMetadata = new File();
		fileMetadata.setTitle(file.getOriginalFilename());
		java.io.File filePath = new java.io.File("upload-dir/" + file.getOriginalFilename());
		FileContent mediaContent = new FileContent(file.getContentType(), filePath);
		File f = service.files().insert(fileMetadata, mediaContent).setFields("id").execute();

		redirectAttributes.addFlashAttribute("linktai", "http://drive.google.com/open?id=" + f.getId());

		return "redirect:/app/insert";

	}

	@ExceptionHandler(StorageFileNotFoundException.class)
	public ResponseEntity handleStorageFileNotFound(StorageFileNotFoundException exc) {
		return ResponseEntity.notFound().build();
	}
}