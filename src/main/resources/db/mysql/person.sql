drop table harrison.person;

CREATE TABLE `harrison`.`person` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NULL,
  PRIMARY KEY (`id`));

insert into harrison.person (name) values ("Harrison");
insert into harrison.person (name) values ("Joe");
commit;