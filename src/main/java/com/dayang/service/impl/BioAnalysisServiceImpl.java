package com.dayang.service.impl;

import com.dayang.domain.BioAnalysisInfo;
import com.dayang.download.MedicinePlatFormDownLoad;
import com.dayang.service.BioAnalysisService;
import com.dayang.util.BioAnalysisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class BioAnalysisServiceImpl implements BioAnalysisService {

    protected static final Logger log = LoggerFactory.getLogger(BioAnalysisServiceImpl.class);

    private static WebDriver driver = null;

    private static final String URL = "http://www.yiqiwu.com/company/list-545.html";

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
    private static String currentWindow;

    @Override
    public void getBioAnalysisCompany() {
        List<BioAnalysisInfo> bioAnalysisInfoList = new ArrayList<>();
        try {
            startHandler(bioAnalysisInfoList);
            //输出产品信息到Excel表
            exportExcelFile(bioAnalysisInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            //输出产品信息到Excel表
            exportExcelFile(bioAnalysisInfoList);
        }
    }

    /**
     * 开始处理
     */
    private void startHandler(List<BioAnalysisInfo> bioAnalysisInfoList) throws Exception {
        //打开网页
        startBrowser();
        //获取生物分析公司列表
        getBioAnalysisCompanyTable(bioAnalysisInfoList);
    }

    /**
     * 开启浏览器, 打开网页
     */
    private void startBrowser() {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(URL);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    }

    /**
     * 获取生物分析公司列表
     *
     * @param bioAnalysisInfoList
     */
    private void getBioAnalysisCompanyTable(List<BioAnalysisInfo> bioAnalysisInfoList) throws Exception {
        //获取下一页标签，当前页码，总页码
        getNextTag();
        log.info("==>> 当前页码: " + currentCount);
        log.info("==>> 下一页: " + nextPage);
        log.info("==>> 总页码: " + count);
        //需要循环的页数
        int sum = Integer.parseInt(count) - Integer.parseInt(currentCount);
        for (int i = 0; i <= sum; i++) {
//        for (int i = 1; i <= 2; i++) {
            //遍历公司列表
            traversalList(bioAnalysisInfoList);
            //点击下一页
            nextPage.click();
            driver = driver.switchTo().window(driver.getWindowHandle());
            currentCount = "";
            count = "";
            nextPage = null;
            //获取下一页,总页数和下一页按钮
            getNextTag();
        }
    }

    /**
     * 获取下一页标签，当前页码，总页码
     */
    private void getNextTag() {
        WebElement divElement = driver.findElement(By.className("m2"));
        WebElement childDivElement = divElement.findElement(By.className("m2l"));
        WebElement pageDivElement = childDivElement.findElement(By.className("pages"));
        //当前页码
        currentCount = pageDivElement.findElement(By.tagName("strong")).getText().trim();
        String allValue = pageDivElement.findElement(By.tagName("cite")).getText();
        count = allValue.substring(allValue.indexOf("/") + 1, allValue.indexOf("页"));
        List<WebElement> aElementList = pageDivElement.findElements(By.tagName("a"));
        //下一页标签
        nextPage = aElementList.get(aElementList.size() - 1);

    }

    //遍历公司列表
    private void traversalList(List<BioAnalysisInfo> bioAnalysisInfoList) throws Exception {
        WebElement divElement = driver.findElement(By.className("m2"));
        WebElement childDivElement = divElement.findElement(By.className("m2l"));
        List<WebElement> listDivElements = childDivElement.findElements(By.className("list"));
        for (WebElement element : listDivElements) {
            BioAnalysisInfo info = new BioAnalysisInfo();
            WebElement tableElement = element.findElement(By.tagName("table"));
            WebElement tbodyElement = tableElement.findElement(By.tagName("tbody"));
            WebElement trElement = tbodyElement.findElement(By.tagName("tr"));
            List<WebElement> tdElementList = trElement.findElements(By.tagName("td"));
            WebElement secondTdElement = tdElementList.get(1);
            WebElement ulElement = secondTdElement.findElement(By.tagName("ul"));
            List<WebElement> liElements = ulElement.findElements(By.tagName("li"));
            WebElement firstLiElement = liElements.get(0);
            WebElement aElement = firstLiElement.findElement(By.tagName("a"));
            String url = aElement.getAttribute("href");
            System.out.println("==>> url: " + url);
            WebElement strongElement = aElement.findElement(By.tagName("strong"));
            String company = strongElement.getText();
            System.out.println("==>> 公司名称: " + company);
            info.setCompany(company);
            //当前句柄
            currentWindow = driver.getWindowHandle();
            //主营
            String major = "";
            if (liElements.size() > 2) {
                WebElement secondLiElement = liElements.get(1);
                major = secondLiElement.getText();
                System.out.println("==>> major: " + major);
                info.setMajor(major);
            }
            //打开生化分析公司详情页
            openBioCompanyDetail(url, info, bioAnalysisInfoList, aElement);
        }
    }

    /**
     * 打开生化分析公司详情页
     *
     * @param url
     * @throws Exception
     */
    private void openBioCompanyDetail(String url, BioAnalysisInfo info, List<BioAnalysisInfo> bioAnalysisInfoList, WebElement aElement) throws Exception {
        //打开网页
        aElement.click();
//        driver.get(url);
        //get all windows
        Set<String> handles = driver.getWindowHandles();
        if (handles.size() < 2) {
            throw new Exception("打开新的标签页失败...");
        }
        WebDriver window = null;
        for (String s : handles) {
            if (s.equals(currentWindow)) {
                continue;
            } else {
                window = driver.switchTo().window(s);
                window.manage().window().maximize();
                window.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                window.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                //获取详情页的参数
                getParamsPage(window, info, bioAnalysisInfoList);
                //close the table window
                window.close();
            }
            driver.switchTo().window(currentWindow);
        }
    }

    /**
     * 获取详情页的参数
     *
     * @param window
     * @param info
     * @param bioAnalysisInfoList
     */
    private void getParamsPage(WebDriver window, BioAnalysisInfo info, List<BioAnalysisInfo> bioAnalysisInfoList) {
        String url = window.getCurrentUrl();
        System.out.println("当前页的url为: " + url);
        List<WebElement> mDivElementList = window.findElements(By.className("m"));
        WebElement mDivElement = mDivElementList.get(5);
        boolean check = elementExist(mDivElement, By.tagName("table"));
        if (!check) {
            return;
        }
        WebElement tableElement = mDivElement.findElement(By.tagName("table"));
        WebElement tbodyElement = tableElement.findElement(By.tagName("tbody"));
        WebElement trElement = tbodyElement.findElement(By.tagName("tr"));
        WebElement firstTdElement = trElement.findElement(By.id("side"));
        List<WebElement> sidebodyElementList = firstTdElement.findElements(By.className("side_body"));
        WebElement thirdSidebodyElement = sidebodyElementList.get(2);
        WebElement ulElement = thirdSidebodyElement.findElement(By.tagName("ul"));
        List<WebElement> liElementList = ulElement.findElements(By.tagName("li"));
        for (WebElement element : liElementList) {
            String value = element.getText();
            if (value.contains("联系人：")) {
                value = value.replace("联系人：", "").trim();
                System.out.println("==>> 联系人: " + value);
                info.setContacts(value);
            } else if (value.contains("电话：")) {
                value = value.replace("电话：", "").trim();
                System.out.println("==>> 电话: " + value);
                info.setPhone(value);
            } else if (value.contains("手机：")) {
                value = value.replace("手机：", "").trim();
                System.out.println("==>> 手机: " + value);
                info.setMobilePhone(value);
            } else if (value.contains("邮件：")) {
                value = value.replace("邮件：", "").trim();
                System.out.println("==>> 邮件:" + value);
                info.setEmail(value);
            }
        }
        if (!bioAnalysisInfoList.contains(info)) {
            bioAnalysisInfoList.add(info);
        }
    }


    /**
     * 判断某元素是否存在
     *
     * @param element
     * @param locator
     * @return
     */
    public boolean elementExist(WebElement element, By locator) {
        try {
            element.findElement(locator);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 输出产品信息
     *
     * @param resultList
     */
    private static void exportExcelFile(List<BioAnalysisInfo> resultList) {
        OutputStream stream = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        Date date = new Date();
        try {
            String dateTime = sdf.format(date);
            String filePath = "D:\\网页爬虫\\生化分析\\";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = filePath + "\\" + "生化分析仪器网" + "_" + dateTime + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("生化分析");

            HSSFCellStyle columnStyle = BioAnalysisUtil.setColumnStyle(workbook);

            //设置内容的样式
            HSSFCellStyle contentStyle = BioAnalysisUtil.setContentStyle(workbook);

            //设置每一列的宽度
            BioAnalysisUtil.setColumnWidth(sheet);

            //设置列名和样式
            BioAnalysisUtil.addTitleData(sheet, columnStyle);

            //冻结表头
            BioAnalysisUtil.freezeHeader(sheet);

            for (int i = 0; i < resultList.size(); i++) {
                BioAnalysisInfo info = resultList.get(i);
                int k = i + 1;
                BioAnalysisUtil.createLine(sheet, contentStyle, k, info);
            }
            File file = new File(fileName);
            stream = new FileOutputStream(file);
            workbook.write(stream);
            System.out.println("导出完成！");
            System.out.println("文件路径为: " + file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("导出失败！");
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
