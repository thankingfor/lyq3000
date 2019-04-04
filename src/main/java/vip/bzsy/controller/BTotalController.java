package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import vip.bzsy.common.CommonResponse;
import vip.bzsy.common.CommonUtils;
import vip.bzsy.common.DataCheckException;
import vip.bzsy.model.LyqDate;
import vip.bzsy.model.LyqTable;
import vip.bzsy.model.Type2Vo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lyf
 * @create 2019-04-03 7:11
 */
@Slf4j
@Controller
@RequestMapping("/bt")
@SuppressWarnings("all")
public class BTotalController {

    /**
     * 按钮二 获取3000个数据
     * 9.获取第0组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copy")
    public void copy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 第一项任务
         */
        long time1 = System.currentTimeMillis();
        List<Integer> anInt = (List<Integer>) intByFile.get("list");
        String dateNum = (String) intByFile.get("dateNum");
        List<LyqTable> list0 = copyStart(anInt);//获取第0组并且按照排序
        List<LyqTable> listMax = new LinkedList<>();//存放最大值
        //开始修改0-3000组的key
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> tables = copySort(list0, group);//本组的降序的tables
            listMax.add(tables.get(0));//把最大的值存放
            list0 = tables;//把list0改成下一组的方便循环
        }
        long time2 = System.currentTimeMillis();
        print("第一个任务耗时：", time1, time2);
        /**
         * 第二个任务 700个
         * 按照lyq_seq进行排序
         */
        long time3 = System.currentTimeMillis();
        List<Type2Vo> type2VoListType = new LinkedList<>();
        Map<Integer, List<LyqTable>> listMap = new HashMap<>();
        Integer groupType2 = 1;
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> listgroup = dataMap.get(group);
            listgroup.sort((x, y) -> x.getLyqSeq() - y.getLyqSeq());
            listMap.put(group, listgroup);
            if (group % groupNum == 0) {
                List<Type2Vo> type2VoList = sortType2(listMap, groupType2);
                type2VoListType.addAll(type2VoList);
                listMap.clear();
                groupType2++;
            }
        }
        //type2VoListType最终排序
        type2VoListType.sort((x, y) -> y.getValue() - x.getValue());
        //进行下载操作
        copyDownMax(response, listMax, type2VoListType, dateNum);
        /**
         * 结果统计
         */
        long endTime2 = System.currentTimeMillis();
        long time4 = System.currentTimeMillis();
        print("第二个任务耗时：", time3, time4);
        print("一共耗时：", time1, time4);
    }

    /**
     * 9.4下载文档
     *
     * @param response
     * @param listMax
     * @param type2VoListType
     * @param dateNum
     * @throws Exception
     */
    public void copyDownMax(HttpServletResponse response, List<LyqTable> listMax,
                            List<Type2Vo> type2VoListType, String dateNum) throws Exception {
        //操作list进行下载  日期号  组  key value
        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("组号");
        row.createCell(1).setCellValue("key");
        row.createCell(2).setCellValue("value");
        row.createCell(4).setCellValue("组号");
        row.createCell(5).setCellValue("序列");
        row.createCell(6).setCellValue("合计");
        //4.遍历数据,创建数据行
        for (LyqTable table : listMax) {
            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(0).setCellValue("第" + table.getLyqGroup() + "组");
            dataRow.createCell(1).setCellValue(table.getLyqKey());
            dataRow.createCell(2).setCellValue(table.getLyqValue());
        }
        for (int i = 0; i < type2VoListType.size(); i++) {
            HSSFRow dataRow = sheet.getRow(i + 1);
            dataRow.createCell(4).setCellValue("第" + type2VoListType.get(i).getGroup() + "组");
            dataRow.createCell(5).setCellValue(type2VoListType.get(i).getSeq());
            dataRow.createCell(6).setCellValue(type2VoListType.get(i).getValue());
        }
        //5.创建文件名
        String fileName = dateNum + ".xls";
        //6.获取输出流对象
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("multipart/form-data");
        //设置请求头
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
    }

    /**
     * 9.3返回前五条数据
     *
     * @param listMap
     * @param group
     * @return
     */
    public List<Type2Vo> sortType2(Map<Integer, List<LyqTable>> listMap, Integer group) {
        Map<Integer, Integer> map = new TreeMap<>();//默认根据key升序
        /**
         * 1.得到21组数据的和
         */
        for (Integer key : listMap.keySet()) {
            List<LyqTable> list = listMap.get(key);
            for (LyqTable lyqTable : list) {
                if (!map.containsKey(lyqTable.getLyqSeq())) {
                    map.put(lyqTable.getLyqSeq(), lyqTable.getLyqValue());
                } else {
                    Integer value = map.get(lyqTable.getLyqSeq()) + lyqTable.getLyqValue();
                    map.put(lyqTable.getLyqSeq(), value);
                }
            }
        }
        /**
         * 根据value排序
         */
        List<Map.Entry<Integer, Integer>> lists = new ArrayList<>(map.entrySet());
        lists.sort((o1, o2) -> o2.getValue() - o1.getValue());
        /**
         * 获取前5条数据
         */
        List<Type2Vo> type2VoList = new LinkedList<>();
        for (int i = 0; i < 5; i++) {
            Type2Vo vo = new Type2Vo();
            vo.setGroup(group);
            vo.setSeq(lists.get(i).getKey());
            vo.setValue(lists.get(i).getValue());
            type2VoList.add(vo);
        }
        return type2VoList;
    }

    /**
     * 9.2对每一组的更新
     * 需要知道前一组的情况
     *
     * @param copyList 前一组的list
     * @return 这一组的list
     */
    public List<LyqTable> copySort(List<LyqTable> copyList, Integer gruop) {
        //long time1 = System.currentTimeMillis();
        //1.获取数据并且按照seq排序
        List<LyqTable> listgroupbySeq = dataMap.get(gruop);
        listgroupbySeq.sort((x, y) -> x.getSeq() - y.getSeq());
        //long time2 = System.currentTimeMillis();
        //print("查询一组数据,并排序", time1, time2);
        //2.把key 复给 下一组 带上0组的seq
        for (int i = 0; i < groupRow; i++) {
            if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }
            listgroupbySeq.get(i).setLyqKey(copyList.get(i).getLyqKey()); //把上一组的key给这一组
            listgroupbySeq.get(i).setLyqSeq(copyList.get(i).getLyqSeq()); //把第零组的序列付给每一组
        }
        //long time3 = System.currentTimeMillis();
        //print("赋值", time2, time3);
        //3.逆向排序
        listgroupbySeq.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        //long time4 = System.currentTimeMillis();
        //print("再次查询一组用时", time1, time4);
        return listgroupbySeq;
    }

    /**
     * 9.1 copy的初始化操作
     */
    public List<LyqTable> copyStart(List<Integer> anInt) {
        List<LyqTable> listgroupbySeq = dataMap.get(0);
        listgroupbySeq.sort((x, y) -> x.getSeq() - y.getSeq());
        //模拟Excel获取3000个数据 并且赋值
        for (int i = 0; i < groupRow; i++) {
            if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }
            listgroupbySeq.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
        }
        //逆向排序
        listgroupbySeq.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        return listgroupbySeq;
    }

    /**
     * 8.上传数据
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping("/upload")
    private CommonResponse getIntByFile(MultipartFile file) throws IOException, DataCheckException {
        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        HSSFSheet sheet = workbook.getSheetAt(0);
        HSSFRow row = sheet.getRow(1);
        String ids = "";
        for (int i = 1; i <= 10; i++) {
            String cellStringValue = CommonUtils.getCellStringValue(row.getCell(i)).trim();
            log.info(cellStringValue);
            if (CommonUtils.isNotEmpty(cellStringValue)) {
                Integer num = Integer.valueOf(cellStringValue.substring(0, 1));
                if (ids == "")
                    ids = num + "";
                else
                    ids = ids + "," + num;
            }
        }
        HSSFCell cellDateNum = row.getCell(0);
        cellDateNum.setCellType(HSSFCell.CELL_TYPE_STRING);
        String dateNum = cellDateNum.getStringCellValue();
        LyqDate lyqDate = new LyqDate()
                .setDateNum(dateNum)
                .setValue(ids);
        log.info("上传数据之日期对象" + lyqDate.toString());
        CommonResponse replace = replace(lyqDate);
        if (replace.getCode() == 0)
            return CommonResponse.fail("日期号码重复了");
        List<Integer> listant = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
            HSSFCell cell = sheet.getRow(i + 1).getCell(12);
            String cellStringValue = CommonUtils.getCellStringValue(cell);
            if (CommonUtils.isNotEmpty(cellStringValue)) {
                cellStringValue = cellStringValue.substring(0, cellStringValue.length() - 2);
                listant.add(Integer.valueOf(cellStringValue));
            }
        }
        //log.info("上传数据之list集合" + listant.toString());
        intByFile.clear();
        intByFile.put("dateNum", lyqDate.getDateNum());
        intByFile.put("list", listant);
        log.info("解析完毕上传的数据" + intByFile.toString());
        return CommonResponse.success();
    }

    /**
     * 7.按钮一 通过 几位数来进行操作
     * key相同的情况下 value为0
     * 其他的value+1
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/replace")
    public CommonResponse replace(LyqDate lyqDate) throws DataCheckException {
        if (checkMap().getCode() == 0) {
            throw new DataCheckException();
        }
        String ids = lyqDate.getValue();
        long count = lyqDateList.stream().filter(x -> x.getDateNum().equals(lyqDate.getDateNum())).count();
        //LyqDate date_num = lyqDate.selectOne(new QueryWrapper<LyqDate>().eq("date_num", lyqDate.getDateNum()));
        if (count > 0) {
            return CommonResponse.fail("期号重复了！！！");
        }
        //把所有的数据改为0 其他的++  然后把日期号存入数据库
        updateToZore(ids.trim());
        lyqDateList.add(lyqDate);
        //lyqDate.insert();
        return CommonResponse.success();
    }

    public void updateToZore(String trim) {
        //变为0 array
        String[] split = trim.split(",");
        Integer[] ids = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            ids[i] = Integer.valueOf(split[i].trim());
        }
        long time1 = System.currentTimeMillis();
        Iterator<Integer> iterator = dataMap.keySet().iterator();
        iterator.forEachRemaining(group -> {
            List<LyqTable> lyqTables = dataMap.get(group);
            List<LyqTable> collect = lyqTables.stream()
                    .map(table -> {
                        if (Arrays.asList(ids).contains(table.getLyqKey()))
                            table.setLyqValue(0);
                        else
                            table.setLyqValue(table.getLyqValue() + 1);
                        return table;
                    }).collect(Collectors.toList());
            dataMap.put(group, collect);
        });
        //Integer row1 = lyqTableMapper.updateToZore(ids);
        long time2 = System.currentTimeMillis();
        print("更新操作（归零和加一）用时：", time1, time2);
    }

    /**
     * 3.查询日期list
     *
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/replace/list")
    public Map<String, Object> replacelist(Integer page, Integer rows) {
        if (lyqDateList.size() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        /**
         * 流的排序分页操作
         */
        List<LyqDate> collect = lyqDateList.stream()
                .sorted((x, y) -> y.getDateNum().compareTo(x.getDateNum()))
                .skip((page - 1) * rows).limit(rows).parallel().collect(Collectors.toList());
//        IPage<LyqDate> pages = lyqDateService.page(new Page<LyqDate>(page, rows),
//                new QueryWrapper<LyqDate>().orderByDesc("date_num"));
        map.clear();
        map.put("rows", collect);
        map.put("total", lyqDateList.size());
        return map;
    }

    /**
     * 4.查询内存中的dataMap
     *
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copy/list")
    public Map<String, Object> copylist(Integer page, Integer rows) throws DataCheckException {
        if (checkMap().getCode() == 0) {
            log.info("内存没有数据，请添加数据...");
            map.clear();
            map.put("rows", null);
            map.put("total", 0);
            return map;
        }
        List<LyqTable> lyqTables = dataMap.get(page - 1);
        //lyqTables.sort((x, y) -> y.getLyqValue() - x.getLyqValue());
        List<LyqTable> collect = lyqTables.stream().sorted((x, y) -> y.getLyqValue() - x.getLyqValue()).limit(rows).collect(Collectors.toList());
        map.clear();
        map.put("rows", collect);
        map.put("total", dataMap.size() * rows);
        return map;
    }


    /**
     * 2.初始化内存数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/init/data")
    public CommonResponse initInsert() {
        dataMap.clear();
        long time1 = System.currentTimeMillis();
        for (int i = 0; i < groupInt; i++) {
            List<LyqTable> init3000 = getInit3000(i);
            dataMap.put(i, init3000);
        }
        log.info("dataMap的大小" + dataMap.keySet().toString());
        long time2 = System.currentTimeMillis();
        print("添加到内存总耗时：", time1, time2);
        return CommonResponse.success();
    }


    public List<LyqTable> getInit3000(Integer group) {
        Random random = new Random();
        List<LyqTable> list = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
            LyqTable lyqTable = new LyqTable();
            lyqTable.setSeq(i + 1);
            lyqTable.setLyqKey(random.nextInt(10));
            lyqTable.setLyqSeq(i + 1);
            lyqTable.setLyqGroup(group);
            lyqTable.setLyqValue(random.nextInt(20));
            list.add(lyqTable);
        }
        return list;
    }

    /**
     * 5.检查dataMap数据是否合法
     */
    @ResponseBody
    @RequestMapping(value = "/check")
    private CommonResponse checkMap() {
        if (dataMap.size() == groupInt) {
            return CommonResponse.success("数据合法");
        }
        return CommonResponse.fail("数据不合法");
    }

    /**
     * 6.加载模板
     *
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @ResponseBody
    @RequestMapping(value = "/donwloadTem")
    public void donwloadTem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //操作list进行下载  日期号  组  key value
        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
        sheet.setColumnWidth(0, 1440 * 3);
        sheet.setColumnWidth(1, 720);
        sheet.setColumnWidth(2, 720);
        sheet.setColumnWidth(3, 720);
        sheet.setColumnWidth(4, 720);
        sheet.setColumnWidth(5, 720);
        sheet.setColumnWidth(6, 720);
        sheet.setColumnWidth(7, 720);
        sheet.setColumnWidth(8, 720);
        sheet.setColumnWidth(9, 720);
        sheet.setColumnWidth(10, 720);
        sheet.setColumnWidth(11, 1440 * 3);
        sheet.setColumnWidth(12, 1440 * 3);
        HSSFRow row = sheet.createRow(0);
        row.createCell(0).setCellValue("期号");
        row.createCell(1).setCellValue("0");
        row.createCell(2).setCellValue("1");
        row.createCell(3).setCellValue("2");
        row.createCell(4).setCellValue("3");
        row.createCell(5).setCellValue("4");
        row.createCell(6).setCellValue("5");
        row.createCell(7).setCellValue("6");
        row.createCell(8).setCellValue("7");
        row.createCell(9).setCellValue("8");
        row.createCell(10).setCellValue("9");
        row.createCell(11).setCellValue("3000数之样式");
        row.createCell(12).setCellValue("需要上传的3000数");
        //赋值3000模板书数
        List<Integer> anInt = get3000Int();
        for (Integer num : anInt) {
            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
            dataRow.createCell(11).setCellValue(num);
        }
        //模板日期
        HSSFRow row1 = sheet.getRow(1);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        row1.createCell(0).setCellValue(format.format(new Date()) + "01");
        row1.createCell(2).setCellValue(1);
        row1.createCell(3).setCellValue(2);
        row1.createCell(4).setCellValue(3);
        //5.创建文件名
        String fileName = "上传模板.xls";
        //6.获取输出流对象
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("multipart/form-data");
        //设置请求头
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
    }


    @ResponseBody
    @RequestMapping(value = "/down/obj")
    public CommonResponse dataMapdonwloadobj(HttpServletRequest request, HttpServletResponse response) {
        long time3 = System.currentTimeMillis();
        if (checkMap().getCode() == 0) {
            return CommonResponse.fail("内存不存在！先初始化数据 或者 读取系统文件");
        }
        File file = new File(outPath);
        File datefile = new File(dataoutPath);
        if (file.exists()) {
            file.delete();
        }
        if (datefile.exists()) {
            datefile.delete();
        }
        //设置请求头
        try (OutputStream outputStream = new FileOutputStream(new File(dataoutPath))
             ; ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(lyqDateList);
            log.info("日期对象写入成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try (OutputStream outputStream = new FileOutputStream(new File(outPath))
             ; ObjectOutputStream oos = new ObjectOutputStream(outputStream)) {
            oos.writeObject(dataMap);
            log.info("内存对象写入成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time4 = System.currentTimeMillis();
        print("一共消耗了", time3, time4);
        return CommonResponse.success("一共消耗了" + (time4 - time3) / 1000 + "秒");
    }

    @ResponseBody
    @RequestMapping(value = "/get/obj")
    public CommonResponse getobj(HttpServletRequest request, HttpServletResponse response) {
        dataMap.clear();
        long time3 = System.currentTimeMillis();
        //如果日期数据存在就加载
        if (new File(dataoutPath).exists()) {
            try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(dataoutPath));) {
                lyqDateList = (List<LyqDate>) oos.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("日期对象加载成功！！！");
        }
        if (!new File(outPath).exists()) {
            return CommonResponse.fail("数据不存在！1.先初始化数据2.下载数据");
        }
        //设置请求头
        try (ObjectInputStream oos = new ObjectInputStream(new FileInputStream(outPath));) {
            dataMap = (Map<Integer, List<LyqTable>>) oos.readObject();
            log.info("内存对象加载成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
        long time4 = System.currentTimeMillis();
        print("一共消耗了", time3, time4);
        return CommonResponse.success("一共用时" + (time4 - time3) / 1000 + "秒");
    }

    public List<Integer> get3000Int() {
        Random random = new Random();
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
            list.add(random.nextInt(10));
        }
        return list;
    }

    public static long print(String value, Long start, Long end) {
        log.info(value + (end - start));
        return end - start;
    }

    @ResponseBody
    @RequestMapping("/printData")
    public void printData() {
        log.info("开始打印data数据 " + dataMap.size());
        Iterator<Integer> iterator = dataMap.keySet().iterator();
        iterator.forEachRemaining(key -> log.info(dataMap.get(key).toString()));
    }

    /**
     * 多少组  3001
     */
    private static Integer groupInt = 3001;
    /**
     * 多少条数据 3000
     */
    private static Integer groupRow = 3000;

    /**
     * 多少组在分一组 21
     */
    private static Integer groupNum = 21;

    private static Map<Integer, List<LyqTable>> dataMap = new HashMap<>();

    private static List<LyqDate> lyqDateList = new ArrayList<>();

    private static Map<String, Object> map = new HashMap<>();

    private static Map<String, Object> intByFile = new HashMap<>();

    private static String outPath = "D:/内存数据.txt";

    private static String dataoutPath = "D:/日期数据.txt";
}
