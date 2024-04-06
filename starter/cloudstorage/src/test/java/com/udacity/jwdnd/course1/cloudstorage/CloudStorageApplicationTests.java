package com.udacity.jwdnd.course1.cloudstorage;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.File;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CloudStorageApplicationTests {

    static final String NOTE_TITLE = "Test create note";
    static final String NOTE_DESCRIPTION = "This is a test note description.";
    static final String FIRST_NAME = "Loc";
    static final String LAST_NAME = "Le";
    static final String USERNAME = "Loclt7";
    static final String PASSWORD = "password";
    static final String CREDENTIAL_URL = "https://www.example.com/credentials/123456";

    @LocalServerPort
    private int port;

    private WebDriver driver;

    @BeforeAll
    static void beforeAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void beforeEach() {
        this.driver = new ChromeDriver();
    }

    @AfterEach
    public void afterEach() {
        if (this.driver != null) {
            driver.quit();
        }
    }

    @Test
    void testGetLoginPage() {
        driver.get("http://localhost:" + this.port + "/login");
        Assertions.assertEquals("Login", driver.getTitle());
    }

    // Test signup and login flow
    @Test
    void testAccessHomePageWithoutLogin() {
        driver.get("http://localhost:" + this.port + "/home");
        Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
    }

    @Test
    void testSignUpANewUserThenLoginAndLogoutAndNoLongerAccessHomePage() {
        doMockSignUp("Not Access To Home Page", "Test", USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);

        WebElement logoutButton = driver.findElement(By.id("logout-button"));
        logoutButton.click();

        Assertions.assertEquals(driver.getCurrentUrl(), "http://localhost:" + this.port + "/login");
        Assertions.assertNotEquals(driver.getCurrentUrl(), "http://localhost:" + this.port + "/home");

    }

    @Test
    void testAddingNoteAndVerifyInNoteList() {
        doMockSignUp(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);
        createNoteAndVerify();
    }

    @Test
    void testEditingANote() {
        doMockSignUp(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);
        createNoteAndVerify();

        WebElement editButton = findEditButtonForExistingNoteByTitle();
        Assertions.assertNotNull(editButton);

        editButton.click();

        // Modify data
        String updateNoteTitle = "Updated new title";
        String updatedDescription = "The updated note description.";

        // Fill out the form with updated data
        fillOutNoteForm(updateNoteTitle, updatedDescription);

        // Saves changes
        WebElement saveButton = driver.findElement(By.id("save-note-button"));
        saveButton.click();

        // Click to home page.
        WebElement linkClickHomePage = driver.findElement(By.id("link-home-page"));
        linkClickHomePage.click();

        // Waiting
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        // Switch to the "nav-notes-tab"
        WebElement notesTabSecond = driver.findElement(By.id("nav-notes-tab"));
        notesTabSecond.click();

        // Verify changes in the note list
        verifyNoteUpdatedInList(updateNoteTitle, updatedDescription);

    }

    @Test
    void testDeleteANote() {
        doMockSignUp("Adding Note", "Testing", "loclt7", "password");
        doLogIn("loclt7", "password");
        createNoteAndVerify();

        WebElement deleteButton = findDeleteButtonForExistingNoteByTitle();
        deleteButton.click();

        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("link-home-page")));

        // Click to home page.
        WebElement linkClickHomePage = driver.findElement(By.id("link-home-page"));
        linkClickHomePage.click();
        Assertions.assertEquals(driver.getCurrentUrl(), "http://localhost:" + this.port + "/home");

        // Verify changes in the note list
        verifyNoteDeletedInList();

    }

    // For credential
    @Test
    void testCreateNewCredential() {
        doMockSignUp(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);
        createNewCredential();
        clickToHomePage();
        switchToCredentialsTab();
        verifyCredentialInList(CREDENTIAL_URL);
    }

    @Test
    void testEditingCredential() {
        doMockSignUp(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);

        createNewCredential();

        // Click to home page.
        clickToHomePage();

        switchToCredentialsTab();


        String updatedCredentialUrl = "https://www.example.com/update-credentials/123456";

        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credentialTable")));
        WebElement editButton = findEditButtonForExistingCredentialUrl(CREDENTIAL_URL);
        editButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));
        WebElement inputCredentialUrl = driver.findElement(By.id("credential-url"));

        inputCredentialUrl.clear();
        inputCredentialUrl.sendKeys(updatedCredentialUrl);

        // Saves changes
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("save-credential-btn")));
        WebElement saveButton = driver.findElement(By.id("save-credential-btn"));
        saveButton.click();

        // Click to home page.
        clickToHomePage();

        // Waiting
        WebDriverWait webDriverWait = new WebDriverWait(driver, 5);
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-credentials-tab")));

        // Switch to the "nav-credentials-tab"
        switchToCredentialsTab();

        // Verify changes in the note list
        verifyCredentialInList(updatedCredentialUrl);

    }

    @Test
    void testDeleteCredential() {
        doMockSignUp(FIRST_NAME, LAST_NAME, USERNAME, PASSWORD);
        doLogIn(USERNAME, PASSWORD);
        createNewCredential();
        clickToHomePage();
        switchToCredentialsTab();

        WebElement deleteButton = findDeleteButtonForCredentialUrl();
        deleteButton.click();

        clickToHomePage();
        switchToCredentialsTab();

        WebElement credentialTable = driver.findElement(By.id("credentialTable"));
        List<WebElement> credentials = credentialTable.findElements(By.tagName("td"));


        WebElement deleteElement = null;
        for (WebElement element : credentials) {
            deleteElement = element.findElement(By.name("delete-btn"));
            if (deleteElement != null) {
                break;
            }
        }

        if (deleteElement != null) {
            WebDriverWait wait = new WebDriverWait(driver, 10);

            wait.until(ExpectedConditions.elementToBeClickable(deleteElement)).click();
            Assertions.assertEquals("Result", driver.getTitle());
        } else {
            // For handle just one test delete
            Assertions.assertNull(deleteElement);
        }

    }

    // Helper methods
    private void switchToCredentialsTab() {
        WebElement credentialTabSecond = driver.findElement(By.id("nav-credentials-tab"));
        credentialTabSecond.click();
    }

    private void createNewCredential() {
        switchToCredentialsTab();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-button")));

        WebElement credentialButton = driver.findElement(By.id("credential-button"));
        credentialButton.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("credential-url")));

        WebElement inputCredentialUrl = driver.findElement(By.id("credential-url"));
        inputCredentialUrl.sendKeys(CREDENTIAL_URL);

        WebElement inputCredentialUsername = driver.findElement(By.id("credential-username"));
        inputCredentialUsername.sendKeys(USERNAME);

        WebElement inputCredentialPassword = driver.findElement(By.id("credential-password"));
        inputCredentialPassword.sendKeys(PASSWORD);

        WebElement inputCredentialSubmit = driver.findElement(By.id("credentialSubmit"));
        inputCredentialSubmit.submit();
    }

    private void clickToHomePage() {
        WebElement linkClickHomePage = driver.findElement(By.id("link-home-page"));
        linkClickHomePage.click();
    }


    private void verifyCredentialInList(String credentialUrl) {
        WebElement credentialTable = driver.findElement(By.id("credentialTable"));
        List<WebElement> credentials = credentialTable.findElements(By.tagName("th"));

        boolean isCredentialCreated = false;
        for (WebElement element : credentials) {
            if (element.getAttribute("innerHTML").equals(credentialUrl)) {
                isCredentialCreated = true;
                break;
            }
        }
        Assertions.assertTrue(isCredentialCreated);
    }

    private void createNoteAndVerify() {

        // Switch to the "nav-notes-tab"
        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        notesTab.click();

        // Click on the new note button
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("new-note-button")));
        WebElement newButtonElement = driver.findElement(By.id("new-note-button"));
        newButtonElement.click();

        // Fill out notes
        WebDriverWait webWaitDriver = new WebDriverWait(driver, 5);
        webWaitDriver.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));

        WebElement inputNoteTitle = driver.findElement(By.id("note-title"));
        inputNoteTitle.sendKeys(CloudStorageApplicationTests.NOTE_TITLE);

        WebElement inputNoteDescription = driver.findElement(By.id("note-description"));
        inputNoteDescription.sendKeys(CloudStorageApplicationTests.NOTE_DESCRIPTION);

        // Save the note
        WebElement saveButton = driver.findElement(By.id("save-note-button"));
        saveButton.click();

        // Show the result page.
        Assertions.assertEquals("Result", driver.getTitle());

        // Click to home page.
        WebElement linkClickHomePage = driver.findElement(By.id("link-home-page"));
        linkClickHomePage.click();

        // Waiting
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        // Switch to the "nav-notes-tab"
        WebElement notesTabSecond = driver.findElement(By.id("nav-notes-tab"));
        notesTabSecond.click();

        // Verify note list
        WebElement notesTable = driver.findElement(By.id("noteTable"));
        List<WebElement> notesList = notesTable.findElements(By.tagName("th"));
        boolean isNoteTitleCreated = false;
        for (WebElement element : notesList) {
            if (element.getAttribute("innerHTML").equals(CloudStorageApplicationTests.NOTE_TITLE)) {
                isNoteTitleCreated = true;
                break;
            }
        }
        Assertions.assertTrue(isNoteTitleCreated);
    }

    private WebElement findEditButtonForExistingNoteByTitle() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='noteTable']//th[text()='" + NOTE_TITLE + "']/parent::tr/td/button")));

        return driver.findElement(By.xpath("//table[@id='noteTable']//th[text()='" + NOTE_TITLE + "']/parent::tr/td/button"));
    }

    private WebElement findEditButtonForExistingCredentialUrl(String credentialUrl) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        By editButtonLocator = By.xpath("//table[@id='credentialTable']//th[text()='" + credentialUrl + "']/parent::tr/td/button");
        wait.until(ExpectedConditions.visibilityOfElementLocated(editButtonLocator));
        return driver.findElement(editButtonLocator);
    }

    private void verifyNoteUpdatedInList(String updateNoteTitle, String updatedDescription) {
        // Wait for the notes tab to be visible
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        // Click on the notes tab
        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        notesTab.click();

        // Find the note table
        WebElement noteTable = driver.findElement(By.id("noteTable"));

        // Build Xpath expressions to locate the updated note's title and description in the table
        String titleXpath = String.format("//table[@id='noteTable']//th[text()='%s']", updateNoteTitle);
        String descriptionXpath = String.format("//table[@id='noteTable']//td[text()='%s']", updatedDescription);

        // Find the title and description elements matching the updated note's information
        WebElement updatedTitleElement = noteTable.findElement(By.xpath(titleXpath));
        WebElement updatedDescriptionElement = noteTable.findElement(By.xpath(descriptionXpath));

        // Assert that both the title and description elements are found
        Assertions.assertNotNull(updatedTitleElement, "Updated note title not found in the list.");
        Assertions.assertNotNull(updatedDescriptionElement, "Updated note description not found in the list.");
    }

    private void fillOutNoteForm(String updateNoteTitle, String updatedDescription) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("note-title")));

        WebElement inputNoteTitle = driver.findElement(By.id("note-title"));
        WebElement inputNoteDescription = driver.findElement(By.id("note-description"));

        inputNoteTitle.clear();
        inputNoteTitle.sendKeys(updateNoteTitle);

        inputNoteDescription.clear();
        inputNoteDescription.sendKeys(updatedDescription);
    }

    private void verifyNoteDeletedInList() {
        // Wait for the notes tab to be visible
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-notes-tab")));

        // Click on the notes tab
        WebElement notesTab = driver.findElement(By.id("nav-notes-tab"));
        notesTab.click();

        // Find note table
        WebElement noteTable = driver.findElement(By.id("noteTable"));
        List<WebElement> notes = noteTable.findElements(By.tagName("td"));
        WebElement deleteElement = null;
        for (WebElement element : notes) {
            deleteElement = element.findElement(By.tagName("a"));
            if (deleteElement != null) {
                break;
            }
        }
        Assertions.assertNull(deleteElement);

    }

    private WebElement findDeleteButtonForExistingNoteByTitle() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='noteTable']//th[text()='" + CloudStorageApplicationTests.NOTE_TITLE + "']/parent::tr/td/a")));
        return driver.findElement(By.xpath("//table[@id='noteTable']//th[text()='" + CloudStorageApplicationTests.NOTE_TITLE + "']/parent::tr/td/a"));
    }

    private WebElement findDeleteButtonForCredentialUrl() {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@id='credentialTable']//th[text()='" + CREDENTIAL_URL + "']/parent::tr/td/a")));
        return driver.findElement(By.xpath("//table[@id='credentialTable']//th[text()='" + CREDENTIAL_URL + "']/parent::tr/td/a"));
    }


    /**
     * PLEASE DO NOT DELETE THIS method.
     * Helper method for Udacity-supplied sanity checks.
     **/
    private void doMockSignUp(String firstName, String lastName, String userName, String password) {
        // Create a dummy account for logging in later.

        // Visit the sign-up page.
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        driver.get("http://localhost:" + this.port + "/signup");
        webDriverWait.until(ExpectedConditions.titleContains("Sign Up"));

        // Fill out credentials
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputFirstName")));
        WebElement inputFirstName = driver.findElement(By.id("inputFirstName"));
        inputFirstName.click();
        inputFirstName.sendKeys(firstName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputLastName")));
        WebElement inputLastName = driver.findElement(By.id("inputLastName"));
        inputLastName.click();
        inputLastName.sendKeys(lastName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        WebElement inputUsername = driver.findElement(By.id("inputUsername"));
        inputUsername.click();
        inputUsername.sendKeys(userName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        WebElement inputPassword = driver.findElement(By.id("inputPassword"));
        inputPassword.click();
        inputPassword.sendKeys(password);

        // Attempt to sign up.
        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("buttonSignUp")));
        WebElement buttonSignUp = driver.findElement(By.id("buttonSignUp"));
        buttonSignUp.click();

		/* Check that the sign up was successful.
		// You may have to modify the element "success-msg" and the sign-up
		// success message below depening on the rest of your code.
		*/
        // Assertions.assertTrue(driver.findElement(By.id("success-msg")).getText().contains("You successfully signed up!"));
    }

    /**
     * PLEASE DO NOT DELETE THIS method.
     * Helper method for Udacity-supplied sanity checks.
     **/
    private void doLogIn(String userName, String password) {
        // Log in to our dummy account.
        driver.get("http://localhost:" + this.port + "/login");
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputUsername")));
        WebElement loginUserName = driver.findElement(By.id("inputUsername"));
        loginUserName.click();
        loginUserName.sendKeys(userName);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("inputPassword")));
        WebElement loginPassword = driver.findElement(By.id("inputPassword"));
        loginPassword.click();
        loginPassword.sendKeys(password);

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("login-button")));
        WebElement loginButton = driver.findElement(By.id("login-button"));
        loginButton.click();

        webDriverWait.until(ExpectedConditions.titleContains("Home"));

    }

    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     * <p>
     * If this test is failing, please ensure that you are handling redirecting users
     * back to the login page after a succesful sign up.
     * Read more about the requirement in the rubric:
     * https://review.udacity.com/#!/rubrics/2724/view
     */
    @Test
    void testRedirection() {
        // Create a test account
        doMockSignUp("Redirection", "Test", "RT", "123");

        // Check if we have been redirected to the log in page.
        Assertions.assertEquals("http://localhost:" + this.port + "/login", driver.getCurrentUrl());
    }

    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     * <p>
     * If this test is failing, please ensure that you are handling bad URLs
     * gracefully, for example with a custom error page.
     * <p>
     * Read more about custom error pages at:
     * https://attacomsian.com/blog/spring-boot-custom-error-page#displaying-custom-error-page
     */
    @Test
    void testBadUrl() {
        // Create a test account
        doMockSignUp("URL", "Test", "UT", "123");
        doLogIn("UT", "123");

        // Try to access a random made-up URL.
        driver.get("http://localhost:" + this.port + "/some-random-page");
        Assertions.assertFalse(driver.getPageSource().contains("Whitelabel Error Page"));
    }


    /**
     * PLEASE DO NOT DELETE THIS TEST. You may modify this test to work with the
     * rest of your code.
     * This test is provided by Udacity to perform some basic sanity testing of
     * your code to ensure that it meets certain rubric criteria.
     * <p>
     * If this test is failing, please ensure that you are handling uploading large files (>1MB),
     * gracefully in your code.
     * <p>
     * Read more about file size limits here:
     * https://spring.io/guides/gs/uploading-files/ under the "Tuning File Upload Limits" section.
     */
    @Test
    void testLargeUpload() {
        // Create a test account
        doMockSignUp("Large File", "Test", "LFT", "123");
        doLogIn("LFT", "123");

        // Try to upload an arbitrary large file
        WebDriverWait webDriverWait = new WebDriverWait(driver, 2);
        String fileName = "upload5m.zip";

        webDriverWait.until(ExpectedConditions.visibilityOfElementLocated(By.id("fileUpload")));
        WebElement fileSelectButton = driver.findElement(By.id("fileUpload"));
        fileSelectButton.sendKeys(new File(fileName).getAbsolutePath());

        WebElement uploadButton = driver.findElement(By.id("uploadButton"));
        uploadButton.click();
        try {
            webDriverWait.until(ExpectedConditions.presenceOfElementLocated(By.id("success")));
        } catch (org.openqa.selenium.TimeoutException e) {
            System.out.println("Large File upload failed");
        }
        Assertions.assertFalse(driver.getPageSource().contains("HTTP Status 403 – Forbidden"));

    }


}
