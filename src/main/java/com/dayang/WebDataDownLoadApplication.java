package com.dayang;

import com.dayang.service.BioAnalysisService;
import com.dayang.service.ChemicalIndustryService;
import com.dayang.service.MedicinePlatformService;
import com.dayang.service.NewCrownMedicineService;
import com.dayang.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@Slf4j
public class WebDataDownLoadApplication {

    protected static final Logger logger = LoggerFactory.getLogger(WebDataDownLoadApplication.class);

    public static void main(String[] args) {
        logger.info("SpringBoot开始加载...");
        SpringApplicationBuilder builder = new SpringApplicationBuilder(WebDataDownLoadApplication.class);
        builder.headless(false).run(args);
//        SpringApplication.run(WebDataDownLoadApplication.class);
        ApplicationContext context = SpringUtil.getApplicationContext();
       /* BioAnalysisService bioAnalysisService = context.getBean(BioAnalysisService.class);
        bioAnalysisService.getBioAnalysisCompany();*/
       /* MedicinePlatformService medicinePlatformService = context.getBean(MedicinePlatformService.class);
        medicinePlatformService.getMedicinePlat();*/
        /*NewCrownMedicineService newCrownMedicineService = context.getBean(NewCrownMedicineService.class);
        newCrownMedicineService.getNewCrownMedicine();*/
        ChemicalIndustryService chemicalIndustryService = context.getBean(ChemicalIndustryService.class);
        chemicalIndustryService.getChemicalCompany();
    }
}
