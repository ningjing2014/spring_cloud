package ${basePackage}.${packageName}.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import ${basePackage}.base.model.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "${tableName}")
public class ${modelName} extends BaseModel {

}
