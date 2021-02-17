package com.dayang.download;

import com.dayang.WebDataDownLoadApplication;
import com.dayang.domain.MedicinePlatInfo;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
@Slf4j
public class MedicinePlatFormDownLoad {

    protected static final Logger log = LoggerFactory.getLogger(MedicinePlatFormDownLoad.class);

    private static WebDriver driver = null;

    private static final String URL = "https://s.yaozh.com/Index/search?type=999&search=";

    /**
     * 总页数
     */
    private static String count = "";

    /**
     * 当前页码
     */
    private static String currentCount = "";

    /**
     * 下一页按钮
     */
    private static WebElement nextPage = null;

    /**
     * 当前句柄
     */
    private static String currentWindowHandler;

    /**
     * 公司页句柄
     */
    private static String companyWindowHandler;

    public static void main(String[] args) {
        try {
            List<MedicinePlatInfo> medicinePlatInfoList = new ArrayList<>();
            //开始处理
            startHandler(medicinePlatInfoList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始处理
     */
    private static void startHandler(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        //打开网页
        startBrowser();
        //获取生物分析公司列表
        getMedicinePlatTable(medicinePlatInfoList);
    }


    /**
     * 开启浏览器, 打开网页
     */
    private static void startBrowser() {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(URL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    }


    /**
     * 获取药智通平台公司列表
     *
     * @param medicinePlatInfoList
     */
    private static void getMedicinePlatTable(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        //获取下一页标签，当前页码，总页码
        getNextTag();

        log.info("==>> 当前页码: " + currentCount);
        log.info("==>> 下一页: " + nextPage);
        log.info("==>> 总页码: " + count);
        //需要循环的页数
        int sum = Integer.parseInt(count) - Integer.parseInt(currentCount);
        for (int i = 0; i <= sum; i++) {
            traversalList(medicinePlatInfoList);
            //点击下一页
            nextPage.click();
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
            driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
            currentCount = "";
            count = "";
            nextPage = null;
            //获取下一页标签，当前页码，总页码
            getNextTag();
        }
    }


    /**
     * 获取下一页标签，当前页码，总页码
     */
    private static void getNextTag() {
        List<WebElement> w1DivElementList = driver.findElements(By.className("w1"));
        WebElement divElement = w1DivElementList.get(2);
        WebElement leftElement = divElement.findElement(By.className("left"));
        WebElement pageElement = leftElement.findElement(By.className("page"));
        WebElement spanElement = pageElement.findElement(By.tagName("span"));
        WebElement childDivElement = spanElement.findElement(By.tagName("div"));
        WebElement currentElement = childDivElement.findElement(By.className("current"));
        currentCount = currentElement.getText().trim();
        List<WebElement> aElementList = childDivElement.findElements(By.tagName("a"));
        count = aElementList.get(aElementList.size() - 1).getText().trim();
        nextPage = aElementList.get(aElementList.size() - 2);
    }


    private static void traversalList(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        List<WebElement> w1DivElementList = driver.findElements(By.className("w1"));
        WebElement divElement = w1DivElementList.get(2);
        WebElement leftElement = divElement.findElement(By.className("left"));
        WebElement left_list = leftElement.findElement(By.className("left-list"));
        WebElement shop_list = left_list.findElement(By.className("shop-list"));
        List<WebElement> itemElementList = shop_list.findElements(By.className("item"));
        for (WebElement itemElement : itemElementList) {
            MedicinePlatInfo info = new MedicinePlatInfo();
            WebElement infoElement = itemElement.findElement(By.className("info"));
            WebElement aElement = infoElement.findElement(By.tagName("a"));
            String url = aElement.getAttribute("href");
            System.out.println("==>> url: " + url);
            String company = aElement.getText();
            System.out.println("==>> 公司名称为: " + company);
            info.setCompany(company);
            List<WebElement> pElementList = infoElement.findElements(By.tagName("p"));
            //主营
            String major = "";
            if (pElementList.size() > 0) {
                major = pElementList.get(0).getText();
                System.out.println("==>> 主营为: " + major);
                info.setMajor(major);
            }
            //当前句柄
            currentWindowHandler = driver.getWindowHandle();

            Thread.sleep(2000);
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_T);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_T);

            //休眠五秒,防止无法拿到所有的句柄
            Thread.sleep(3000);

            //重定向到公司页
            directCompanyPage(url, info, medicinePlatInfoList);
        }
    }


    /**
     * 重定向到公司页
     *
     * @param url
     * @param info
     * @param medicinePlatInfoList
     * @throws Exception
     */
    private static void directCompanyPage(String url, MedicinePlatInfo info, List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        //get all windows
        Set<String> handles = driver.getWindowHandles();
        if (handles.size() < 2) {
            throw new Exception("打开新的标签页失败...");
        }
        WebDriver companyWindow = null;
        for (String s : handles) {
            if (s.equals(currentWindowHandler)) {
                continue;
            } else {
                companyWindow = driver.switchTo().window(s);
                System.out.println("输入网址...");
                companyWindow.get(url);
                Thread.sleep(10000);
                companyWindow.manage().window().maximize();
                companyWindow.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                companyWindow.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                companyWindowHandler = companyWindow.getWindowHandle();
                WebElement w1Element = companyWindow.findElement(By.className("w1"));
                WebElement right775Element = w1Element.findElement(By.className("right775"));
                WebElement introductionElement = right775Element.findElement(By.className("introduction"));
                WebElement aElement = introductionElement.findElement(By.tagName("a"));

                //获取更多详情页
                getMoreDetailPage(companyWindow, info, medicinePlatInfoList, aElement);
                //close the table window
                companyWindow.close();
            }
            driver.switchTo().window(currentWindowHandler);
        }
    }


    /**
     * 获取更多详情页
     *
     * @param companyWindow
     * @param info
     * @param medicinePlatInfoList
     * @throws Exception
     */
    private static void getMoreDetailPage(WebDriver companyWindow, MedicinePlatInfo info, List<MedicinePlatInfo> medicinePlatInfoList, WebElement aElement) throws Exception {
        //打开网页
        aElement.click();
        //get all windows
        Set<String> handles = companyWindow.getWindowHandles();
        if (handles.size() < 3) {
            throw new Exception("打开新的标签页失败...");
        }

    }
}
