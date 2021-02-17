package com.dayang.service.impl;

import com.dayang.domain.BioAnalysisInfo;
import com.dayang.domain.MedicinePlatInfo;
import com.dayang.service.MedicinePlatformService;
import com.dayang.util.BioAnalysisUtil;
import com.dayang.util.MedicineUtil;
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

import java.awt.*;
import java.awt.event.KeyEvent;
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
public class MedicinePlatformServiceImpl implements MedicinePlatformService {

    protected static final Logger log = LoggerFactory.getLogger(MedicinePlatformServiceImpl.class);

    private static WebDriver driver = null;

    private static final String URL = "https://s.yaozh.com/Index/search?type=999&search=&p=477";

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

    @Override
    public void getMedicinePlat() {
        List<MedicinePlatInfo> medicinePlatInfoList = new ArrayList<>();
        try {
            //开始处理
            startHandler(medicinePlatInfoList);
            //输出产品信息到Excel表
            exportExcelFile(medicinePlatInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            //输出产品信息到Excel表
            exportExcelFile(medicinePlatInfoList);
        }
    }


    /**
     * 开始处理
     */
    private void startHandler(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        //打开网页
        startBrowser();
        //获取生物分析公司列表
        getMedicinePlatTable(medicinePlatInfoList);
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
     * 获取药智通平台公司列表
     *
     * @param medicinePlatInfoList
     */
    private void getMedicinePlatTable(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
        //获取下一页标签，当前页码，总页码
        getNextTag();
        log.info("==>> 当前页码: " + currentCount);
        log.info("==>> 下一页: " + nextPage);
        log.info("==>> 总页码: " + count);
        //需要循环的页数
        int sum = Integer.parseInt(count) - Integer.parseInt(currentCount);
        for (int i = 0; i <= sum; i++) {
//        for (int i = 0; i <= 200; i++) {
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
    private void getNextTag() {
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


    private void traversalList(List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
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
            System.out.println("==>> 网址为: " + url);
            info.setUrl(url);
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
    private void directCompanyPage(String url, MedicinePlatInfo info, List<MedicinePlatInfo> medicinePlatInfoList) throws Exception {
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
                Thread.sleep(3000);
                companyWindow.manage().window().maximize();
                companyWindow.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                companyWindow.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
                companyWindowHandler = companyWindow.getWindowHandle();

                WebElement w1Element = companyWindow.findElement(By.className("w1"));
                WebElement right775Element = w1Element.findElement(By.className("right775"));
                WebElement introductionElement = right775Element.findElement(By.className("introduction"));
                WebElement aElement = introductionElement.findElement(By.tagName("a"));
//                String detailUrl = aElement.getAttribute("href");
//                info.setUrl(detailUrl);
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
    private void getMoreDetailPage(WebDriver companyWindow, MedicinePlatInfo info, List<MedicinePlatInfo> medicinePlatInfoList, WebElement aElement) throws Exception {
        //打开网页
        aElement.click();
        //get all windows
        Set<String> handles = companyWindow.getWindowHandles();
        if (handles.size() < 3) {
            throw new Exception("打开新的标签页失败...");
        }
        WebDriver moreDetailWindow = null;
        for (String s : handles) {
            if (s.equals(currentWindowHandler) || s.equals(companyWindowHandler)) {
                continue;
            } else {
                moreDetailWindow = companyWindow.switchTo().window(s);
                moreDetailWindow.manage().window().maximize();
                moreDetailWindow.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                moreDetailWindow.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

                //获取参数
                getParamsPage(moreDetailWindow, info, medicinePlatInfoList);
                moreDetailWindow.close();
            }
            companyWindow.switchTo().window(companyWindowHandler);
        }
    }

    /**
     * 获取参数
     * @param moreDetailWindow
     * @param info
     * @param medicinePlatInfoList
     */
    private void getParamsPage(WebDriver moreDetailWindow, MedicinePlatInfo info, List<MedicinePlatInfo> medicinePlatInfoList) {
        WebElement w1Element = moreDetailWindow.findElement(By.className("w1"));
        WebElement right775Element = w1Element.findElement(By.className("right775"));
        WebElement rbuiElement = right775Element.findElement(By.id("rbui"));
        WebElement rbui_list_element = rbuiElement.findElement(By.className("rbui-list"));
        WebElement listtElement = rbui_list_element.findElement(By.id("listt"));
        List<WebElement> lisElementList = listtElement.findElements(By.className("lis"));
        WebElement tablesElement = null;
        WebElement tableElement = null;
        WebElement tbodyElement = null;
        List<WebElement> trElementList = null;
        WebElement trElement = null;
        List<WebElement> tdElementList = null;
        for (int i = 0; i < lisElementList.size(); i++) {
            if (i == 1) {
                continue;
            }
            if (i == 0) {
                WebElement firstLisElement = lisElementList.get(i);
                tablesElement = firstLisElement.findElement(By.className("table1-s"));
                tableElement = tablesElement.findElement(By.tagName("table"));
                tbodyElement = tableElement.findElement(By.tagName("tbody"));
                trElementList = tbodyElement.findElements(By.tagName("tr"));
                trElement = trElementList.get(trElementList.size() - 2);
                tdElementList = trElement.findElements(By.tagName("td"));
                String address = tdElementList.get(0).getText();
                System.out.println("==>> 地址为: " + address);
                info.setAddress(address);
            } else if (i == 2) {
                WebElement thirdLisElement = lisElementList.get(i);
                tablesElement = thirdLisElement.findElement(By.className("table1-s"));
                tableElement = tablesElement.findElement(By.tagName("table"));
                tbodyElement = tableElement.findElement(By.tagName("tbody"));
                trElementList = tableElement.findElements(By.tagName("tr"));
                trElement = trElementList.get(trElementList.size() - 1);
                tdElementList = trElement.findElements(By.tagName("td"));
                String phone = tdElementList.get(0).getText();
                System.out.println("==>> 联系电话: " + phone);
                info.setPhone(phone);
                String email = tdElementList.get(1).getText();
                System.out.println("==>> E-mail: " + email);
                info.setEmail(email);
            }
        }
        if (!medicinePlatInfoList.contains(info)) {
            medicinePlatInfoList.add(info);
        }
    }


    /**
     * 判断某个元素是否存在
     */
    public boolean isJudgingElement(WebDriver webDriver, By by) {
        try {
            webDriver.findElement(by);
            return true;
        } catch (Exception e) {
            System.out.println("不存在此元素");
            return false;
        }
    }


    /**
     * 输出产品信息
     *
     * @param resultList
     */
    private static void exportExcelFile(List<MedicinePlatInfo> resultList) {
        OutputStream stream = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date date = new Date();
        try {
            String dateTime = sdf.format(date);
            String filePath = "D:\\网页爬虫\\药智通平台\\";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = filePath + "\\" + "药智通平台" + "_" + dateTime + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("药智通平台");

            HSSFCellStyle columnStyle = MedicineUtil.setColumnStyle(workbook);

            //设置内容的样式
            HSSFCellStyle contentStyle = MedicineUtil.setContentStyle(workbook);

            //设置每一列的宽度
            MedicineUtil.setColumnWidth(sheet);

            //设置列名和样式
            MedicineUtil.addTitleData(sheet, columnStyle);

            //冻结表头
            MedicineUtil.freezeHeader(sheet);

            for (int i = 0; i < resultList.size(); i++) {
                MedicinePlatInfo info = resultList.get(i);
                int k = i + 1;
                MedicineUtil.createLine(sheet, contentStyle, k, info);
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
