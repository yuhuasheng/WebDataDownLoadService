package com.dayang.service.impl;

import com.dayang.domain.ChemicalIndustryInfo;
import com.dayang.domain.MedicinePlatInfo;
import com.dayang.service.ChemicalIndustryService;
import com.dayang.util.ChemicalIndustryUtil;
import com.dayang.util.MedicineUtil;
import com.dayang.util.Tools;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChemicalIndustryServiceImpl implements ChemicalIndustryService {

    protected static final Logger log = LoggerFactory.getLogger(ChemicalIndustryServiceImpl.class);

    private static WebDriver driver = null;

    private static final String URL = "https://www.chem960.com/company/companylist_area_1.shtml";

    /**
     * 文件路径
     */
    private static final String FILEPATH = "src/main/resources/file/province.txt";

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
    public void getChemicalCompany() {
        /*String filePath = Tools.createFile(FILEPATH);
        if ("".equals(filePath)) {
            return;
        }
        //开启浏览器， 打开网页
        startBrowser(URL);
        //获取省份, 存放到province.txt文本中
        recordUrlList(filePath);*/
        //获取文本
        getUrlList();
    }


    /**
     * 开启浏览器, 打开网页
     */
    private void startBrowser(String url) {
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get(url);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);
    }


    /**
     * 获取省份, 存放到province.txt文本中
     *
     * @param filePath
     */
    private void recordUrlList(String filePath) {
        WebElement kj_content = driver.findElement(By.className("kj_content"));
        WebElement kj_content_inner = kj_content.findElement(By.className("kj_content_inner"));
        WebElement wp_le1 = kj_content_inner.findElement(By.className("wp_le1"));
        WebElement layui_colla_item = wp_le1.findElement(By.className("layui-colla-item"));
        WebElement layui_colla_content = layui_colla_item.findElement(By.className("layui-colla-content"));
        WebElement cprivincebox = layui_colla_content.findElement(By.className("cprivincebox"));
        WebElement row = cprivincebox.findElement(By.className("row"));
        List<WebElement> elementList = row.findElements(By.className("layui-col-xs6"));
//        for (WebElement element : elementList) {
        for (int i = 1; i < elementList.size(); i++) {
            WebElement element = elementList.get(i);
            String city = element.getText();
            System.out.println("==>> 省份: " + city);
            WebElement aElement = element.findElement(By.tagName("a"));
            String url = aElement.getAttribute("href");
            System.out.println("==>> url: " + url);
            Tools.write(filePath, url);
        }
    }


    /**
     * 获取网址清单
     */
    private void getUrlList() {
        List<String> resultList = Tools.getTextContent("/file/province.txt");
        List<ChemicalIndustryInfo> chemicalIndustryInfoList = new ArrayList<>();
        for (String url : resultList) {
            System.out.println("==>> 网址: " + url);
            try {
                //开始处理
                startHandler(url, chemicalIndustryInfoList);
                //输出产品信息到Excel表
                exportExcelFile(chemicalIndustryInfoList);
                break;
            } catch (Exception e) {
                e.printStackTrace();
                //输出产品信息到Excel表
                exportExcelFile(chemicalIndustryInfoList);
                break;
            } finally {
//                driver.close();
            }
        }
    }

    private void startHandler(String url, List<ChemicalIndustryInfo> chemicalIndustryInfoList) throws Exception {
        //开启浏览器， 打开网页
        startBrowser(url);
        //获取公司列表信息
        getCompanyListInfo(chemicalIndustryInfoList);
    }

    /**
     * 获取公司列表信息
     *
     * @param chemicalIndustryInfoList
     * @throws Exception
     */
    private void getCompanyListInfo(List<ChemicalIndustryInfo> chemicalIndustryInfoList) throws Exception {
        //获取下一页按钮
        getNextPage();
        if ("".equals(currentCount)) { //代表只有一页
            //遍历公司列表
            traversalCompanyList(chemicalIndustryInfoList);
            return;
        }
       while (Integer.parseInt(currentCount) < Integer.parseInt(count)) {
//        while (Integer.parseInt(currentCount) < 15) {
            //遍历公司列表
            traversalCompanyList(chemicalIndustryInfoList);
            //点击下一页
            nextPage.click();
            driver = driver.switchTo().window(driver.getWindowHandle());
            currentCount = "";
            count = "";
            nextPage = null;
            //获取下一页,总页数和下一页按钮
            getNextPage();
        }
    }

    /**
     * 获取下一页,总页数和下一页按钮
     */
    private void getNextPage() {
        WebElement kj_content = driver.findElement(By.className("kj_content"));
        WebElement kj_content_inner = kj_content.findElement(By.className("kj_content_inner"));
        WebElement wp_ri1 = kj_content_inner.findElement(By.className("wp_ri1"));
        WebElement layui_colla_item = wp_ri1.findElement(By.className("layui-colla-item"));
        WebElement layui_colla_content = layui_colla_item.findElement(By.className("layui-colla-content"));
        WebElement pager = layui_colla_content.findElement(By.className("pager"));
        WebElement layui_box = pager.findElement(By.className("layui-box"));
        WebElement divElement = layui_box.findElement(By.tagName("div"));
        count = divElement.getAttribute("data-pagecount"); //获取总页码
        System.out.println("==>> count: " + count);
        boolean check = checkElement(divElement, By.tagName("span"));
        if (!check) {
            return;
        }
        WebElement span = divElement.findElement(By.tagName("span"));
        currentCount = span.getText();
        System.out.println("==>> currentCount: " + currentCount);
        List<WebElement> aElementList = divElement.findElements(By.tagName("a"));
        nextPage = aElementList.get(aElementList.size() - 1);
    }


    /**
     * 遍历公司列表
     *
     * @param chemicalIndustryInfoList
     */
    private void traversalCompanyList(List<ChemicalIndustryInfo> chemicalIndustryInfoList) throws Exception {
        WebElement kj_content = driver.findElement(By.className("kj_content"));
        WebElement kj_content_inner = kj_content.findElement(By.className("kj_content_inner"));
        WebElement wp_ri1 = kj_content_inner.findElement(By.className("wp_ri1"));
        WebElement layui_colla_item = wp_ri1.findElement(By.className("layui-colla-item"));
        WebElement layui_colla_content = layui_colla_item.findElement(By.className("layui-colla-content"));
        WebElement table = layui_colla_content.findElement(By.tagName("table"));
        WebElement tbody = table.findElement(By.tagName("tbody"));
        List<WebElement> trElementList = tbody.findElements(By.tagName("tr"));
        for (WebElement trElement : trElementList) {
            ChemicalIndustryInfo info = new ChemicalIndustryInfo();
            List<WebElement> tdElementList = trElement.findElements(By.tagName("td"));
            WebElement tdElement = tdElementList.get(1);
            WebElement aElement = tdElement.findElement(By.tagName("a"));
            String companyName = aElement.getText();
            System.out.println("==>> 公司名称: " + companyName);
            info.setCompanyName(companyName);
            //当前句柄
            currentWindow = driver.getWindowHandle();
            String companyUrl = aElement.getAttribute("href");
            System.out.println("==>> 公司网址为: " + companyUrl);

            //详情页
            detailPage(chemicalIndustryInfoList, info, aElement);
        }
    }

    /**
     * 详情页
     *
     * @param chemicalIndustryInfoList
     * @param info
     * @param aElement
     * @throws Exception
     */
    private void detailPage(List<ChemicalIndustryInfo> chemicalIndustryInfoList, ChemicalIndustryInfo info, WebElement aElement) throws Exception {
        Thread.sleep(8000);
        aElement.click();
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

                //获取参数
                getParamsPage(chemicalIndustryInfoList, info, window);

                //close the table window
                window.close();
            }
            driver.switchTo().window(currentWindow);
        }
    }

    /**
     * 获取参数
     *
     * @param chemicalIndustryInfoList
     * @param info
     * @param window
     */
    private void getParamsPage(List<ChemicalIndustryInfo> chemicalIndustryInfoList, ChemicalIndustryInfo info, WebDriver window) {
        String url = window.getCurrentUrl();
        System.out.println("当前页的url为: " + url);
        //判断某个元素是否存在
        boolean flag = isJudgingElement(window, By.className("kj_banner"));
        if (!flag) {
            return;
        }
        WebElement kj_banner = window.findElement(By.className("kj_banner"));
        WebElement company_container = kj_banner.findElement(By.className("company_container"));
        WebElement max_width_700 = company_container.findElement(By.className("max-width-700"));
        WebElement pElement = max_width_700.findElement(By.tagName("p"));
        String value = pElement.getText();
        System.out.println("==>> 公司简介: " + value);
        info.setCompanyProfile(value);

        WebElement kj_content = window.findElement(By.className("kj_content"));
        WebElement pleft = kj_content.findElement(By.className("pleft"));
        List<WebElement> layui_colla_itemList = pleft.findElements(By.className("layui-colla-item"));
        WebElement itemElement = null;
        for (WebElement element : layui_colla_itemList) {
            String text = element.getText();
            if (text.contains("联系方式")) {
                itemElement = element;
                break;
            }
        }
        WebElement layui_colla_content = itemElement.findElement(By.className("layui-colla-content"));
        WebElement compaycopayconat = layui_colla_content.findElement(By.className("compaycopayconat"));
        List<WebElement> spanElementList = compaycopayconat.findElements(By.tagName("span"));
        for (WebElement element : spanElementList) {
            String text = element.getText();
            System.out.println("==>> text: " + text);
            if (text.contains("地址：")) {
                info.setAddress(text.replace("地址：", ""));
            } else if (text.contains("固 话：")) {
                info.setTelephone(text.replace("固 话：", ""));
            } else if (text.contains("手 机：")) {
                info.setPhone(text.replace("手 机：", ""));
            } else if (text.contains("邮箱：")) {
                info.setEmail(text.replace("邮箱：", ""));
            }
        }
        String text = compaycopayconat.getText();
        System.out.println("==>> text: " + text);
        String[] split = text.split("\\n");
        for (String options : split) {
            if (options.contains("企业性质：")) {
                info.setEnterpriseNature(options.replace("企业性质：", ""));
            } else if (options.contains("省 市：")) {
                info.setProvince(options.replace("省 市：", ""));
            } else if (options.contains("联系人：")) {
                info.setContacts(options.replace("联系人：", ""));
            }
        }
        if (!chemicalIndustryInfoList.contains(info)) {
            chemicalIndustryInfoList.add(info);
        }
    }


    /**
     * 去除字符串中的制表符\t,回车\n,换行\r
     *
     * @param str
     * @return
     */
    private String replaceBlank(String str) {
        String dest = "";
        if (dest != null) {
            Pattern p = Pattern.compile("\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll(" ");
        }
        return dest;
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
     * 判断某个元素是否存在
     */
    public boolean checkElement(WebElement element, By by) {
        try {
            element.findElement(by);
            return true;
        } catch (Exception e) {
            System.out.println("不存在此元素");
            return false;
        }
    }


    /**
     * 输出公司信息
     *
     * @param resultList
     */
    private void exportExcelFile(List<ChemicalIndustryInfo> resultList) {
        OutputStream stream = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");
        Date date = new Date();
        try {
            String dateTime = sdf.format(date);
            String filePath = "D:\\网页爬虫\\960化工网厂商\\";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = filePath + "\\" + "960化工网厂商" + "_" + dateTime + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("960化工网厂商");

            HSSFCellStyle columnStyle = ChemicalIndustryUtil.setColumnStyle(workbook);

            //设置内容的样式
            HSSFCellStyle contentStyle = ChemicalIndustryUtil.setContentStyle(workbook);

            //设置每一列的宽度
            ChemicalIndustryUtil.setColumnWidth(sheet);

            //设置列名和样式
            ChemicalIndustryUtil.addTitleData(sheet, columnStyle);

            //冻结表头
            ChemicalIndustryUtil.freezeHeader(sheet);

            for (int i = 0; i < resultList.size(); i++) {
                ChemicalIndustryInfo info = resultList.get(i);
                int k = i + 1;
                ChemicalIndustryUtil.createLine(sheet, contentStyle, k, info);
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
