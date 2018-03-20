package com.ln.xproject.generate;

import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.ln.xproject.util.FileUtils;
import com.ln.xproject.util.StringUtils;

import freemarker.template.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Generate {

    private static String BASE_PACKAGE = "com.ln.xproject";
    private static String TEMPLATE_PACKAGE = "com.ln.xproject.generate";
    private static String BASE_PKG_NAME="verify";

    public static void main(String[] args) {
        String packageName = "application";
        String modelName = "ApplicationAuditLog";
        generate(packageName, modelName);
    }

    private static String getBasePath() {
        return Generate.class.getResource("/").getPath().substring(0).replace("target/classes/", "src/main/java/");
    }

    private static String getFullPath(String packageName) {
        return getBasePath() + packageName.replaceAll("\\.", "/");
    }

    private static String getTemplatePath() {
        return getFullPath(TEMPLATE_PACKAGE);
    }

    private static void generate(String packageName, String modelName) {

        log.info("begin generate...");
        log.info("package: {}, model: {}", packageName, modelName);

        generateModel(packageName, modelName);
        generateRepository(packageName, modelName);
        generateService(packageName, modelName);
        generateServiceImpl(packageName, modelName);
        generateConstants(packageName, modelName);
        generateVo(packageName, modelName);

        log.info("generate end...");
    }

    private static void generateModel(String packageName, String modelName) {
        log.info("generate model");
        String fullPath = createPackage(packageName + ".model");
        generateFile(fullPath, modelName, "ModelTemplate.ftl", packageName, modelName);
    }

    private static void generateRepository(String packageName, String modelName) {
        log.info("generate repository");
        String fullPath = createPackage(packageName + ".repository");
        generateFile(fullPath, modelName + "Repository", "RepositoryTemplate.ftl", packageName, modelName);
    }

    private static void generateService(String packageName, String modelName) {
        log.info("generate service");
        String fullPath = createPackage(packageName + ".service");
        generateFile(fullPath, modelName + "Service", "ServiceTemplate.ftl", packageName, modelName);
    }

    private static void generateServiceImpl(String packageName, String modelName) {
        log.info("generate service impl");
        String fullPath = createPackage(packageName + ".service.impl");
        generateFile(fullPath, modelName + "ServiceImpl", "ServiceImplTemplate.ftl", packageName, modelName);
    }

    private static void generateConstants(String packageName, String modelName) {
        createPackage(packageName + ".constants");
    }

    private static void generateVo(String packageName, String modelName) {
        createPackage(packageName + ".vo");
    }

    private static String createPackage(String packageName) {
        String fullPackageName = BASE_PACKAGE + "." + packageName;
        String fullPath = getFullPath(fullPackageName);
        mkdirIfNull(fullPath);
        return fullPath;
    }

    private static void mkdirIfNull(String path) {
        File dir = FileUtils.getFile(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @SuppressWarnings("deprecation")
    private static void generateFile(String fullPath, String fileName, String template, String packageName,
            String modelName) {
        try {
            File file = FileUtils.getFile(StringUtils.join(fullPath, "/", fileName, ".java"));
            if (file.exists()) {
                log.error("{} is exists", file.getName());
                return;
            }
            file.createNewFile();

            Map<String, String> model = new HashMap<>();
            model.put("packageName", packageName);
            model.put("modelName", modelName);
            model.put("tableName", getTableName(modelName));
            model.put("basePackage",BASE_PACKAGE);

            Configuration configuration = new Configuration();
            configuration.setClassicCompatible(true);
            configuration.setDirectoryForTemplateLoading(new File(getTemplatePath()));

            String code = FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate(template), model);

            FileUtils.write(file, code, Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getTableName(String modelName) {
        StringBuilder tableName = new StringBuilder();
        String[] parts = StringUtils.splitByCharacterTypeCamelCase(modelName);
        for (int i = 0; i < parts.length; i++) {
            tableName.append(parts[i].toLowerCase());
            if (i != parts.length - 1) {
                tableName.append("_");
            }
        }
        return tableName.toString();
    }

}
