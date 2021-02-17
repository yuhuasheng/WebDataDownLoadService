package com.dayang.service.impl;

import com.dayang.domain.BioAnalysisInfo;
import com.dayang.domain.MedicinePlatInfo;
import com.dayang.domain.NewCrownMedicineInfo;
import com.dayang.service.NewCrownMedicineService;
import com.dayang.util.AntibodymedicineUtil;
import com.dayang.util.BioAnalysisUtil;
import com.dayang.util.ProteinMedicineUtil;
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
public class NewCrownMedicineServiceImpl implements NewCrownMedicineService {

    protected static final Logger log = LoggerFactory.getLogger(NewCrownMedicineServiceImpl.class);

    private static WebDriver driver = null;

    private static final String URL = "http://www.atagenix.com/ata_product_list-1-8.html";

    private static int index = 0;
    /**
     * 当前句柄
     */
    private static String currentWindow;

    @Override
    public void getNewCrownMedicine() {
        List<NewCrownMedicineInfo> newCrownMedicineInfoList = new ArrayList<>();
        try {
            //开始处理
            startHandler(newCrownMedicineInfoList);
            //输出产品信息到Excel表
            exportExcelFile(newCrownMedicineInfoList);
        } catch (Exception e) {
            e.printStackTrace();
            //输出产品信息到Excel表
            exportExcelFile(newCrownMedicineInfoList);
        } finally {
            System.out.println("==>>当前运行的行数: " + index);
        }
    }

    /**
     * 开始处理
     */
    private void startHandler(List<NewCrownMedicineInfo> newCrownMedicineInfoList) throws Exception {
        //打开网页
        startBrowser();
        //获取新冠病毒相关产品清单
        getMedicineList(newCrownMedicineInfoList);
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
     * 获取新冠病毒相关产品清单
     *
     * @param newCrownMedicineInfoList
     */
    private void getMedicineList(List<NewCrownMedicineInfo> newCrownMedicineInfoList) throws Exception {
        WebElement warpElement = driver.findElement(By.className("warp"));
        List<WebElement> centreElementList = warpElement.findElements(By.className("centre"));
        WebElement centreElement = null;
        for (WebElement element : centreElementList) {
            String value = element.getText();
            if (value.contains("新冠肺炎疫情的爆发，牵动着世界人民的心")) {
                centreElement = element;
                break;
            }
        }
        if (null == centreElement) {
            return;
        }
        WebElement product_list = centreElement.findElement(By.className("product_list"));
        WebElement covid_info = product_list.findElement(By.className("covid-info"));
        WebElement table = covid_info.findElement(By.tagName("table"));
        WebElement tbody = table.findElement(By.tagName("tbody"));
        List<WebElement> trElementList = tbody.findElements(By.tagName("tr"));
        Boolean flag = false;
//        for (WebElement trElement : trElementList) {
        for (int i = 0; i < trElementList.size(); i++) {
//        for (int i = 0; i < 68; i++) {
            WebElement trElement = trElementList.get(i);
            NewCrownMedicineInfo info = new NewCrownMedicineInfo();
            List<WebElement> tdElementList = trElement.findElements(By.tagName("td"));
            WebElement lastTdElement = tdElementList.get(tdElementList.size() - 1);
            WebElement aElement = lastTdElement.findElement(By.tagName("a"));
            String url = aElement.getAttribute("href");
            System.out.println("==>> url: " + url);
            String catalogNumber = aElement.getText();
            System.out.println("==>> Catalog#： " + catalogNumber + ", 当前序号为: " + i);
            index++;
            //校验货号是否已经遍历过
           /* Boolean flag = checkDraw(catalogNumber);
            if (flag) {
                continue;
            }*/
            /*if ("ATMA10320Mo".equals(catalogNumber)) {
                System.out.println("新冠蛋白遍历完成...");
                break;
            }*/
            if ("http://www.atagenix.com/product_detail-73460.html".equals(url)) { //判断循环遍历是否到达该网页，到达该网页就爬取网页信息
                flag = true;
            }
            if (!flag) {
                continue;
            }
            //当前句柄
            currentWindow = driver.getWindowHandle();

            Thread.sleep(2000);
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_T);
            robot.keyRelease(KeyEvent.VK_CONTROL);
            robot.keyRelease(KeyEvent.VK_T);

            //休眠五秒,防止无法拿到所有的句柄
            Thread.sleep(2000);

            //重定向到药物详情页
            directMedicineDetail(url, info, newCrownMedicineInfoList, catalogNumber);
        }
    }

    /**
     * 校对货号
     *
     * @param product
     * @return
     */
    private Boolean checkDraw(String product) {
        List<String> list = Tools.getTextContent("/product/product.txt");
        for (String value : list) {
            if (value.equals(product)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 重定向到药物详情页
     *
     * @param url
     * @param info
     * @param newCrownMedicineInfoList
     */
    private void directMedicineDetail(String url, NewCrownMedicineInfo info, List<NewCrownMedicineInfo> newCrownMedicineInfoList, String catalogNumber) throws Exception {
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
                System.out.println("输入网址..." + ", 当前货号为: " + catalogNumber);
                window.get(url);
                window.manage().window().maximize();
                window.manage().timeouts().implicitlyWait(60, TimeUnit.SECONDS);
                window.manage().timeouts().pageLoadTimeout(60, TimeUnit.SECONDS);

                //获取详情页的参数
                getParamsPage(window, info, newCrownMedicineInfoList, catalogNumber);

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
     * @param newCrownMedicineInfoList
     */
    private void getParamsPageBak(WebDriver window, NewCrownMedicineInfo info, List<NewCrownMedicineInfo> newCrownMedicineInfoList) {
        WebElement warpElement = window.findElement(By.className("warp"));
        List<WebElement> centreElementList = warpElement.findElements(By.className("centre"));
        WebElement centreElement = null;
        for (WebElement element : centreElementList) {
            String value = element.getText();
            System.out.println("==>> value: " + value);
            if (value.contains("产品概述")) {
                centreElement = element;
                break;
            }
        }
        if (null == centreElement) {
            return;
        }
        WebElement product_detail = centreElement.findElement(By.className("product_detail"));
        WebElement pboard = product_detail.findElement(By.className("pboard"));
        WebElement pboard_c = pboard.findElement(By.className("pboard_c"));
        List<WebElement> tabElementList = pboard_c.findElements(By.className("tab"));
        WebElement tab = tabElementList.get(0);
        List<WebElement> contentElementList = tab.findElements(By.className("c_content"));
        WebElement contentElement = null;
        for (int i = 0; i < contentElementList.size() - 1; i++) {
           /* if (i == 1 || i == 2 || i == 4 || i == 5) {
                continue;
            }*/
            contentElement = contentElementList.get(i);
            if (i == 0) {
                setProteinProductOverviewParams(contentElement, info);
            } else {
                boolean flag = elementExist(contentElement, By.tagName("div"));
                if (!flag) { //假如不存在子div标签，则继续下次遍历循环
                    continue;
                }
                setProteinBackgroundParams(contentElement, info, newCrownMedicineInfoList);
            }
        }
        if (!newCrownMedicineInfoList.contains(info)) {
            newCrownMedicineInfoList.add(info);
        }
    }


    /**
     * 获取详情页的参数
     *
     * @param window
     * @param info
     * @param newCrownMedicineInfoList
     */
    private void getParamsPage(WebDriver window, NewCrownMedicineInfo info, List<NewCrownMedicineInfo> newCrownMedicineInfoList, String catalogNumber) {
        info.setProductNumber(catalogNumber);
        WebElement warpElement = window.findElement(By.className("warp"));
        List<WebElement> centreElementList = warpElement.findElements(By.className("centre"));
        WebElement centreElement = null;
        for (WebElement element : centreElementList) {
            String value = element.getText();
            System.out.println("==>> value: " + value);
            if (value.contains("产品概述")) {
                centreElement = element;
                break;
            }
        }
        if (null == centreElement) {
            return;
        }
        WebElement product_detail = centreElement.findElement(By.className("product_detail"));
        WebElement pboard = product_detail.findElement(By.className("pboard"));
        WebElement pboard_c = pboard.findElement(By.className("pboard_c"));
        List<WebElement> tabElementList = pboard_c.findElements(By.className("tab"));
        WebElement tab = tabElementList.get(0);
        List<WebElement> contentElementList = tab.findElements(By.className("c_content"));
        List<WebElement> titleElementList = tab.findElements(By.className("c_title"));
        WebElement contentElement = null;
        WebElement titleElement = null;
        for (int i = 0; i < titleElementList.size(); i++) {
            titleElement = titleElementList.get(i);
            contentElement = contentElementList.get(i);
            String title = titleElement.getText();
            System.out.println("==>> 标题: " + title);
            if (title.contains("产品概述")) {
                setAntibodyProductOverviewParams(contentElement, info);
            } else if (title.contains("背景")) {
                setAntibodyBackgroundParams(contentElement, info);
            } else if (title.contains("产品性能")) {
                setAntibodyProductPerformanceParams(contentElement, info);
            } else if (title.contains("应用")) {
                setAntibodyApplicationParams(contentElement, info);
            }
        }
        if (!newCrownMedicineInfoList.contains(info)) {
            newCrownMedicineInfoList.add(info);
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
     * 设置产品概述字段(新冠蛋白)
     *
     * @param contentElement
     * @param info
     */
    private void setProteinProductOverviewParams(WebElement contentElement, NewCrownMedicineInfo info) {
        List<WebElement> divElementList = contentElement.findElements(By.tagName("div"));
        for (int i = 0; i < divElementList.size(); i++) {
            WebElement element = divElementList.get(i);
            String options = element.getText();
            WebElement nextElemet = null;
            if (options.contains("产品名称")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 产品名称: " + value);
                info.setProductName(value);
            } else if (options.contains("货号")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 产品货号: " + value);
                info.setProductNumber(value);
            } else if (options.contains("描述")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 描述: " + value);
                info.setDescription(value);
            } else if (options.contains("表达系统")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 表达系统: " + value);
                info.setExpressionSystem(value);
            } else if (options.contains("种属")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 种属: " + value);
                info.setSpecies(value);
            } else if (options.contains("Accession")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> Accession: " + value);
                info.setAccession(value);
            } else if (options.contains("别名")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 别名: " + value);
                info.setAlias(value);
            } else if (options.contains("预测分子量")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 预测分子量: " + value);
                info.setPredictedMolecularWeight(value);
            } else if (options.contains("实际分子量")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 实际分子量: " + value);
                info.setActualMolecularWeight(value);
            } else if (options.contains("纯度")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 纯度: " + value);
                info.setPurity(value);
            } else if (options.contains("内毒素")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 内毒素: " + value);
                info.setEndotoxin(value);
            } else if (options.contains("制剂")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 制剂: " + value);
                info.setPreparation(value);
            } else if (options.contains("运输方式")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 运输方式: " + value);
                info.setTypeShipping(value);
            } else if (options.contains("稳定性&储存")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 稳定性&储存: " + value);
                info.setStabilityStorage(value);
            } else if (options.contains("复溶")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 复溶: " + value);
                info.setReconstitution(value);
            } else if (options.contains("应用")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 应用: " + value);
                info.setApplication(value);
            }
        }
    }

    /**
     * 设置背景字段(新冠蛋白)
     *
     * @param contentElement
     * @param info
     * @param newCrownMedicineInfoList
     */
    private void setProteinBackgroundParams(WebElement contentElement, NewCrownMedicineInfo info, List<NewCrownMedicineInfo> newCrownMedicineInfoList) {
        WebElement divElement = contentElement.findElement(By.tagName("div"));
        String backGroundText = divElement.getText();
        System.out.println("==>> 背景: " + backGroundText);
        info.setBackground(backGroundText);
    }

    /**
     * 设置产品概述字段(抗体)
     *
     * @param contentElement
     * @param info
     */
    private void setAntibodyProductOverviewParams(WebElement contentElement, NewCrownMedicineInfo info) {
        List<WebElement> divElementList = contentElement.findElements(By.tagName("div"));
        for (int i = 0; i < divElementList.size(); i++) {
            WebElement element = divElementList.get(i);
            String options = element.getText();
            WebElement nextElemet = null;
            if (options.contains("产品名称")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 产品名称: " + value);
                info.setProductName(value);
            } else if (options.contains("货号")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 产品货号: " + value);
                info.setProductNumber(value);
            } else if (options.contains("描述")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 描述: " + value);
                info.setDescription(value);
            } else if (options.contains("别名")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 别名: " + value);
                info.setAlias(value);
            } else if (options.contains("内毒素")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 内毒素: " + value);
                info.setEndotoxin(value);
            } else if (options.contains("稳定性&储存")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 稳定性&储存: " + value);
                info.setStabilityStorage(value);
            } else if (options.contains("来源")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 来源: " + value);
                info.setSource(value);
            } else if (options.contains("特异性")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 特异性: " + value);
                info.setSpecificity(value);
            } else if (options.contains("亚型")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 亚型: " + value);
                info.setSubtype(value);
            } else if (options.contains("宿主")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 宿主: " + value);
                info.setHost(value);
            } else if (options.contains("克隆性")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 克隆性: " + value);
                info.setClonality(value);
            } else if (options.contains("克隆号")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 克隆号: " + value);
                info.setCloneNumber(value);
            } else if (options.contains("偶连物")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 偶连物: " + value);
                info.setConjugates(value);
            } else if (options.contains("种属反应性")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 种属反应性: " + value);
                info.setSpeciesReactivity(value);
            } else if (options.contains("应用实验")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 应用实验: " + value);
                info.setApplicationExperiment(value);
            } else if (options.contains("免疫原")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 免疫原: " + value);
                info.setImmunogen(value);
            }
        }
    }

    /**
     * 设置背景字段(抗体)
     *
     * @param contentElement
     * @param info
     */
    private void setAntibodyBackgroundParams(WebElement contentElement, NewCrownMedicineInfo info) {
        WebElement divElement = contentElement.findElement(By.tagName("div"));
        String backGroundText = divElement.getText();
        System.out.println("==>> 背景: " + backGroundText);
        info.setBackground(backGroundText);
    }

    /**
     * 设置产品性能(抗体)
     *
     * @param contentElement
     * @param info
     */
    private void setAntibodyProductPerformanceParams(WebElement contentElement, NewCrownMedicineInfo info) {
        List<WebElement> divElementList = contentElement.findElements(By.tagName("div"));
        for (int i = 0; i < divElementList.size(); i++) {
            WebElement element = divElementList.get(i);
            String options = element.getText();
            WebElement nextElemet = null;
            if (options.contains("状态")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 状态: " + value);
                info.setState(value);
            } else if (options.contains("储存溶液")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 储存溶液: " + value);
                info.setStorageSolution(value);
            } else if (options.contains("浓度")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 浓度: " + value);
                info.setConcentration(value);
            } else if (options.contains("分子量")) {
                nextElemet = divElementList.get(i + 1);
                String value = nextElemet.getText();
                System.out.println("==>> 分子量: " + value);
                info.setMolecularWeight(value);
            }
        }
    }

    /**
     * 设置应用参数(应用实验及稀释比例)
     *
     * @param contentElement
     * @param info
     */
    private void setAntibodyApplicationParams(WebElement contentElement, NewCrownMedicineInfo info) {
        List<WebElement> divElementList = contentElement.findElements(By.tagName("div"));
        for (WebElement element : divElementList) {
            String value = element.getText();
            System.out.println("==>> value: " + value);
            if (value.contains("应用实验及稀释比例")) {
                continue;
            }
            info.setApplicationDilutionRatio(value);
        }
    }

    /**
     * 输出产品信息
     *
     * @param resultList
     */
    private static void exportExcelFile(List<NewCrownMedicineInfo> resultList) {
        OutputStream stream = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日  HH时mm分ss秒");
        Date date = new Date();
        try {
            String dateTime = sdf.format(date);
            String filePath = "D:\\网页爬虫\\新冠抗体\\";
            File dir = new File(filePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String fileName = filePath + "\\" + "新冠抗体" + "_" + dateTime + ".xls";
            HSSFWorkbook workbook = new HSSFWorkbook();
            HSSFSheet sheet = workbook.createSheet("新冠抗体");

            HSSFCellStyle columnStyle = AntibodymedicineUtil.setColumnStyle(workbook);

            //设置内容的样式
            HSSFCellStyle contentStyle = AntibodymedicineUtil.setContentStyle(workbook);

            //设置每一列的宽度
            AntibodymedicineUtil.setColumnWidth(sheet);

            //设置列名和样式
            AntibodymedicineUtil.addTitleData(sheet, columnStyle);

            //冻结表头
            AntibodymedicineUtil.freezeHeader(sheet);

            for (int i = 0; i < resultList.size(); i++) {
                NewCrownMedicineInfo info = resultList.get(i);
                int k = i + 1;
                AntibodymedicineUtil.createLine(sheet, contentStyle, k, info);
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
