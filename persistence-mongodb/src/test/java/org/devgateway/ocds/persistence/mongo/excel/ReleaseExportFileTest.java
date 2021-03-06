package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.devgateway.ocds.persistence.mongo.ReleasePackage;
import org.devgateway.ocds.persistence.mongo.spring.json2object.JsonToObject;
import org.devgateway.ocds.persistence.mongo.spring.json2object.ReleasePackageJsonToObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;

/**
 * @author idobre
 * @since 6/7/16
 */
public class ReleaseExportFileTest {
    @Test
    public void createWorkbook() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("json/release-excel-export.json").getFile());
        final JsonToObject releasePackageJsonToObject = new ReleasePackageJsonToObject(file);

        final ReleasePackage releasePackage = (ReleasePackage) releasePackageJsonToObject.toObject();

        final ExcelFile releaseExcelFile = new ReleaseExportFile(new ArrayList<>(releasePackage.getReleases()));
        final Workbook workbook = releaseExcelFile.createWorkbook();
        // try (FileOutputStream outputStream = new FileOutputStream("/Users/ionut/Downloads/ocds-export.xlsx")) {
        //     workbook.write(outputStream);
        // }

        Assert.assertEquals("Number of sheets", 6, workbook.getNumberOfSheets());

        Assert.assertNotNull("release sheet", workbook.getSheet("release"));
        Assert.assertNotNull("tender sheet", workbook.getSheet("tender"));
        Assert.assertNotNull("award sheet", workbook.getSheet("award"));
        Assert.assertNotNull("item sheet", workbook.getSheet("usaitem"));
        Assert.assertNotNull("contract sheet", workbook.getSheet("contract"));

        Assert.assertEquals("release id", "ocds-213czf-000-00001-01-planning",
                workbook.getSheet("release").getRow(3).getCell(0).toString());
        Assert.assertEquals("release id", "ocds-213czf-000-00001-06-implementation",
                workbook.getSheet("release").getRow(5).getCell(0).toString());

        Assert.assertEquals("tender parent", "release - ocds-213czf-000-00001",
                workbook.getSheet("tender").getRow(1).getCell(0).toString());
        Assert.assertEquals("tender id", "ocds-213czf-000-00001-01-planning",
                workbook.getSheet("tender").getRow(1).getCell(1).toString());


        Assert.assertEquals("award number of rows", 4, workbook.getSheet("award").getLastRowNum());

        Assert.assertEquals("item number of rows", 9, workbook.getSheet("usaitem").getLastRowNum());

        Assert.assertEquals("contract flatten organizatio id", "E09000003 | E09000003",
                workbook.getSheet("contract").getRow(2).getCell(17).toString());
    }
}
