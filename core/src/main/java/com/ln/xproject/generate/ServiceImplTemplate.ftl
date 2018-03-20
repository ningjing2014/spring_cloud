package ${basePackage}.${packageName}.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ${basePackage}.base.service.impl.BaseServiceImpl;
import ${basePackage}.${packageName}.model.${modelName};
import ${basePackage}.${packageName}.repository.${modelName}Repository;
import ${basePackage}.${packageName}.service.${modelName}Service;

@Service
@Transactional
public class ${modelName}ServiceImpl extends BaseServiceImpl<${modelName}, ${modelName}Repository> implements ${modelName}Service {

    @Autowired
    @Override
    protected void setRepository(${modelName}Repository repository) {
        super.repository = repository;
    }

}
