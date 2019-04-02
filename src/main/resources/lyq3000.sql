/*
Navicat MySQL Data Transfer

Source Server         : 本机
Source Server Version : 50711
Source Host           : localhost:3306
Source Database       : lyq3000

Target Server Type    : MYSQL
Target Server Version : 50711
File Encoding         : 65001

Date: 2019-04-02 21:39:34
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for lyq_date
-- ----------------------------
DROP TABLE IF EXISTS `lyq_date`;
CREATE TABLE `lyq_date` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `date_num` varchar(255) NOT NULL COMMENT '期号',
  `value` varchar(255) NOT NULL COMMENT '上传的值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for lyq_table
-- ----------------------------
DROP TABLE IF EXISTS `lyq_table`;
CREATE TABLE `lyq_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `seq` int(4) NOT NULL COMMENT '3000条的序列',
  `lyq_group` int(11) NOT NULL,
  `lyq_key` int(2) NOT NULL,
  `lyq_value` int(6) NOT NULL COMMENT '变值',
  `lyq_seq` int(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15997000 DEFAULT CHARSET=utf8;
