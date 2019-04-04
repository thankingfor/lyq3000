package vip.bzsy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author lyf
 * @create 2019-04-01 15:24
 */
@Slf4j
@Controller
public class ATotalController {



    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "index";
    }

//    @ResponseBody
//    @RequestMapping(value = "/test")
//    public CommonResponse test() {
//        return CommonResponse.success("测试成功");
//    }

//
//    /**
//     * 按钮二 获取3000个数据
//     * 1.获取第0组
//     *
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/copy")
//    public CommonResponse copy(MultipartFile file, HttpServletRequest request, HttpServletResponse response) throws Exception {
//        long startTime1 = System.currentTimeMillis();
//        List<Integer> anInt = (List<Integer>) intByFile.get("list");
//        String dateNum =(String)intByFile.get("dateNum");
//        /**
//         * 第一项任务
//         */
//        List<LyqTable> list0 = copyStart(anInt);//给第0组赋值
//        List<LyqTable> listMax = new LinkedList<>();//存放最大值
//        //Map<String, List<LyqTable>> map = new HashMap<>(); //list都是按照value降序排序的，未修改过的
//        //map.put("0", list0);
//        //开始修改0-3000组的key
//        for (int group = 1; group < groupInt; group++) {
//            long startTime = System.currentTimeMillis();
//            List<LyqTable> tables = copySort(list0, group);//本组的降序的tables
//            listMax.add(tables.get(0));//把最大的值存放
//            //map.put(group + "", tables);//放入map中
//            list0 = tables;//把list0改成下一组的方便循环
//            long endTime = System.currentTimeMillis();
//            log.info("转移："+group+"组"+(endTime-startTime));
//        }
//        long endTime1 = System.currentTimeMillis();
//        log.info("第一个任务耗时："+(endTime1-startTime1));
//        /**
//         * 第二个任务 700个
//         * 按照lyq_seq进行排序
//         */
//        List<Type2Vo> type2VoListType = new LinkedList<>();
//        Map<String, List<LyqTable>> listMap = new HashMap<>();
//        Integer groupType2 = 1;
//        for (int group = 1; group < groupInt; group++) {
//            List<LyqTable> listgroup = lyqTableService.list(new QueryWrapper<LyqTable>()
//                    .eq("lyq_group", group).orderByAsc("lyq_seq"));
//            listMap.put(group + "", listgroup);
//            if (group % groupNum == 0) {
//                List<Type2Vo> type2VoList = sortType2(listMap, groupType2);
//                type2VoListType.addAll(type2VoList);
//                listMap.clear();
//                groupType2++;
//            }
//        }
//        //type2VoListType最终排序
//        type2VoListType.sort((x, y) -> y.getValue() - x.getValue());
//        //进行下载操作
//        copyDownMax(response, listMax, type2VoListType,dateNum);//操作list进行下载  日期号  组  key value
//        long endTime2 = System.currentTimeMillis();
//        log.info("第二个任务耗时："+(endTime2-endTime1));
//        log.info("一共耗时："+(endTime2-startTime1));
//        return CommonResponse.success();
//    }
//
//    /**
//     * 上传数据
//     * @param file
//     * @return
//     * @throws IOException
//     */
//    @ResponseBody
//    @RequestMapping("/upload")
//    private CommonResponse getIntByFile(MultipartFile file) throws IOException {
//        HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
//        HSSFSheet sheet = workbook.getSheetAt(0);
//        HSSFRow row = sheet.getRow(1);
//        String ids = "";
//        for (int i = 1; i <= 10; i++) {
//            log.info(CommonUtils.getCellStringValue(row.getCell(i)));
//            String cellStringValue = CommonUtils.getCellStringValue(row.getCell(i)).trim();
//            if (cellStringValue != ""){
//                log.info(cellStringValue);
//                Integer num = Integer.valueOf(cellStringValue.substring(0,1));
//                if (ids=="")
//                    ids = num+"";
//                else
//                    ids = ids + "," + num;
//            }
//        }
//        HSSFCell cellDateNum = row.getCell(0);
//        cellDateNum.setCellType(HSSFCell.CELL_TYPE_STRING);
//        String dateNum = cellDateNum.getStringCellValue();
//        LyqDate lyqDate = new LyqDate()
//                .setDateNum(dateNum)
//                .setValue(ids);
//        //log.info(lyqDate.toString());
//        long startTime = System.currentTimeMillis();
//        CommonResponse replace = replace(lyqDate);
//        long endTime = System.currentTimeMillis();
//        log.info("替换操作  归零 加加"+(endTime-startTime));
//        if (replace.getCode() == 0)
//            return CommonResponse.fail("日期号码重复了");
//        List<Integer> listant = new LinkedList<>();
//        for (int i = 0; i < groupRow; i++) {
//            HSSFCell cell = sheet.getRow(i + 1).getCell(12);
//            String cellStringValue = CommonUtils.getCellStringValue(cell);
//            cellStringValue = cellStringValue.substring(0,cellStringValue.length()-2);
//            //log.info(cellStringValue);
//            listant.add(Integer.valueOf(cellStringValue));
//        }
//        intByFile.clear();
//        intByFile.put("dateNum", lyqDate.getDateNum());
//        intByFile.put("list", listant);
//        return CommonResponse.success();
//    }
//
//    /**
//     * 返回前五条数据
//     *
//     * @param listMap
//     * @param group
//     * @return
//     */
//    public List<Type2Vo> sortType2(Map<String, List<LyqTable>> listMap, Integer group) {
//        Map<Integer, Integer> map = new TreeMap<>();//默认升序
//        for (String key : listMap.keySet()) {
//            List<LyqTable> list = listMap.get(key);
//            for (LyqTable lyqTable : list) {
//                if (!map.containsKey(lyqTable.getLyqSeq())) {
//                    map.put(lyqTable.getLyqSeq(), lyqTable.getLyqValue());
//                } else {
//                    Integer value = map.get(lyqTable.getLyqSeq()) + lyqTable.getLyqValue();
//                    map.put(lyqTable.getLyqSeq(), value);
//                }
//            }
//        }
//        // 排序
//        // 降序排序比较器
//        Comparator<Map.Entry<Integer, Integer>> keyComparator = new Comparator<Map.Entry<Integer, Integer>>() {
//            @Override
//            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
//
//                return o2.getValue() - o1.getValue();
//            }
//        };
//        List<Map.Entry<Integer, Integer>> lists = new ArrayList<>(map.entrySet());
//        Collections.sort(lists, keyComparator);
//        //log.info(lists.toString());
//        List<Type2Vo> type2VoList = new LinkedList<>();
//        for (int i = 0; i < 5; i++) {
//            Type2Vo vo = new Type2Vo();
//            vo.setGroup(group);
//            vo.setSeq(lists.get(i).getKey());
//            vo.setValue(lists.get(i).getValue());
//            type2VoList.add(vo);
//        }
//        return type2VoList;
//    }
//
//    public void copyDownMax(HttpServletResponse response, List<LyqTable> listMax,
//                            List<Type2Vo> type2VoListType,String dateNum) throws Exception {
//        //操作list进行下载  日期号  组  key value
//        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
//        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
//        HSSFRow row = sheet.createRow(0);
//        row.createCell(0).setCellValue("组号");
//        row.createCell(1).setCellValue("key");
//        row.createCell(2).setCellValue("value");
//        row.createCell(4).setCellValue("组号");
//        row.createCell(5).setCellValue("序列");
//        row.createCell(6).setCellValue("合计");
//        //4.遍历数据,创建数据行
//        for (LyqTable table : listMax) {
//            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
//            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
//            dataRow.createCell(0).setCellValue("第" + table.getLyqGroup() + "组");
//            dataRow.createCell(1).setCellValue(table.getLyqKey());
//            dataRow.createCell(2).setCellValue(table.getLyqValue());
//        }
//        for (int i = 0; i < type2VoListType.size(); i++) {
//            HSSFRow dataRow = sheet.getRow(i + 1);
//            dataRow.createCell(4).setCellValue("第" + type2VoListType.get(i).getGroup() + "组");
//            dataRow.createCell(5).setCellValue(type2VoListType.get(i).getSeq());
//            dataRow.createCell(6).setCellValue(type2VoListType.get(i).getValue());
//        }
//        //5.创建文件名
//        String fileName = dateNum+".xls";
//        //6.获取输出流对象
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//        response.setContentType("multipart/form-data");
//        //设置请求头
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//    }
//
//    /**
//     * 对每一组的更新
//     * 需要知道前一组的情况
//     *
//     * @param copyList 前一组的list
//     * @return 这一组的list
//     */
//    public List<LyqTable> copySort(List<LyqTable> copyList, Integer gruop) {
//        long time1 = System.currentTimeMillis();
//        List<LyqTable> listgroupbySeq = lyqTableService.listByGroupOrderByAsc(gruop);
////        List<LyqTable> listgroupbySeq = lyqTableService.list(new QueryWrapper<LyqTable>()
////                .eq("lyq_group", gruop).orderByAsc("seq"));
//        long time2 = System.currentTimeMillis();
//        print("查询一组数据",time1,time2);
//        //把key 复给 下一组 带上0组的seq
//        for (int i = 0; i < groupRow; i++) {
//            if (listgroupbySeq.get(i).getSeq() != i + 1) {
//                throw new RuntimeException("顺序不一致");
//            }
//            listgroupbySeq.get(i).setLyqKey(copyList.get(i).getLyqKey()); //把上一组的key给这一组
//            listgroupbySeq.get(i).setLyqSeq(copyList.get(i).getLyqSeq()); //把第零组的序列付给每一组
//        }
//        //批量更新  先删除 后添加
//        long time3 = System.currentTimeMillis();
//        print("循环赋值",time2,time3);
//        lyqTableService.remove(new QueryWrapper<LyqTable>().eq("lyq_group", gruop));
//        long time6 = System.currentTimeMillis();
//        print("删除",time3,time6);
//        //lyqTableService.updateBatchById(listgroupbySeq);
//        lyqTableService.saveBatchByMyself(listgroupbySeq);
//        long time4 = System.currentTimeMillis();
//        //按照降序查询出本组所有的list
//        print("批量更新",time3,time4);
//        listgroupbySeq.sort((x,y)->y.getLyqValue()-x.getLyqValue());
////        List<LyqTable> listgroup = lyqTableService.list(new QueryWrapper<LyqTable>()
////                .eq("lyq_group", gruop).orderByDesc("lyq_value"));
//        long time5 = System.currentTimeMillis();
//        print("再次查询一次数据//自排序",time4,time5);
//        return listgroupbySeq;
//    }
//
//    /**
//     * copy的初始化操作
//     */
//    public List<LyqTable> copyStart(List<Integer> anInt) {
//        List<LyqTable> listgroupbySeq = lyqTableService.list(new QueryWrapper<LyqTable>()
//                .eq("lyq_group", 0).orderByAsc("seq"));
//        //模拟Excel获取3000个数据
//        for (int i = 0; i < groupRow; i++) {
//            if (listgroupbySeq.get(i).getSeq() != i + 1) {
//                throw new RuntimeException("顺序不一致");
//            }
//            listgroupbySeq.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
//        }
//        //批量更新
//        lyqTableService.updateBatchById(listgroupbySeq);
//        List<LyqTable> lyq_gruop0 = lyqTableService.list(new QueryWrapper<LyqTable>()
//                .eq("lyq_group", 0).orderByDesc("lyq_value"));
//        return lyq_gruop0;
//    }
//
//
//    /**
//     * 按钮一 通过 几位数来进行操作
//     * key相同的情况下 value为0
//     * 其他的value+1
//     *
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/replace")
//    public CommonResponse replace(LyqDate lyqDate) {
//        String ids = lyqDate.getValue();
//        LyqDate date_num = lyqDate.selectOne(new QueryWrapper<LyqDate>().eq("date_num", lyqDate.getDateNum()));
//        if (CommonUtils.isNotEmpty(date_num)) {
//            return CommonResponse.fail("期号重复了！！！");
//        }
//        //log.info(lyqDate.toString());
//        //把所有的数据改为0 其他的++
//        Integer toZore = lyqTableService.updateToZore(ids.trim());
//        lyqDate.insert();
//        return CommonResponse.success(toZore);
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "/replace/list")
//    public Map<String, Object> replacelist(Integer page, Integer rows) {
//        IPage<LyqDate> pages = lyqDateService.page(new Page<LyqDate>(page, rows),
//                new QueryWrapper<LyqDate>().orderByDesc("date_num"));
//        map.clear();
//        map.put("rows", pages.getRecords());
//        map.put("total", pages.getTotal());
//        return map;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "/copy/list")
//    public Map<String, Object> copylist(Integer page) {
//        List<LyqTable> list = lyqTableService.list(new QueryWrapper<LyqTable>()
//                .eq("lyq_group", page - 1).orderByAsc("seq"));
//        int count = lyqTableService.count();
//        map.clear();
//        map.put("rows", list);
//        map.put("total", count);
//        return map;
//    }
//
//    /**
//     * 初始化数据
//     *
//     * @return
//     */
//    @ResponseBody
//    @RequestMapping(value = "/init")
//    public CommonResponse init() {
//        //获取3000条数据
//        long startTime1 = System.currentTimeMillis();
//        boolean b = initInsert();
//        if (!b) {
//            return CommonResponse.fail("插入失败");
//        }
//        long endTime1 = System.currentTimeMillis();
//        log.info("初始化数据库耗时："+(endTime1-startTime1));
//        return CommonResponse.success((endTime1-startTime1)/1000);
//    }
//
//    public boolean initInsert() {
//        //删除表中的所有数据
//        boolean remove = lyqTableService.remove(null);
//        //添加第0-3000组
//        //List<LyqTable> list = new LinkedList<>();
//        for (int i = 0; i < groupInt; i++) {
//            //long startTime = System.currentTimeMillis();
//            //list.addAll(getInit3000(i));
//            lyqTableService.saveBatchByMyself(getInit3000(i));
//            //long endTime = System.currentTimeMillis();
//            //log.info("初始化数据库耗时："+(endTime-startTime));
//        }
//
//        //lyqTableService.saveBatch(list);
//        //lyqTableService.saveBatchByMyself(list);
//
//        return remove;
//    }
//
//    public List<Integer> get3000Int() {
//        Random random = new Random();
//        List<Integer> list = new LinkedList<>();
//        for (int i = 0; i < groupRow; i++) {
//            list.add(random.nextInt(10));
//        }
//        return list;
//    }
//
//    public List<LyqTable> getInit3000(Integer group) {
//        log.info("第" + group + "组");
//        Random random = new Random();
//        List<LyqTable> list = new LinkedList<>();
//
//        for (int i = 0; i < groupRow; i++) {
//            LyqTable lyqTable = new LyqTable();
//            lyqTable.setSeq(i + 1);
//            lyqTable.setLyqKey(random.nextInt(10));
//            lyqTable.setLyqSeq(i + 1);
//            lyqTable.setLyqGroup(group);
//            lyqTable.setLyqValue(random.nextInt(20));
//            list.add(lyqTable);
//        }
//        return list;
//    }
//
//    @ResponseBody
//    @RequestMapping(value = "/donwloadTem")
//    public CommonResponse donwloadTem(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        log.info("i come in");
//        //操作list进行下载  日期号  组  key value
//        HSSFWorkbook workbook = new HSSFWorkbook();//1.在内存中操作excel文件
//        HSSFSheet sheet = workbook.createSheet();//2.创建工作谱
//        sheet.setColumnWidth(0, 1440 * 3);
//        sheet.setColumnWidth(1, 720);
//        sheet.setColumnWidth(2, 720);
//        sheet.setColumnWidth(3, 720);
//        sheet.setColumnWidth(4, 720);
//        sheet.setColumnWidth(5, 720);
//        sheet.setColumnWidth(6, 720);
//        sheet.setColumnWidth(7, 720);
//        sheet.setColumnWidth(8, 720);
//        sheet.setColumnWidth(9, 720);
//        sheet.setColumnWidth(10, 720);
//        sheet.setColumnWidth(11, 1440 * 3);
//        sheet.setColumnWidth(12, 1440 * 3);
//        HSSFRow row = sheet.createRow(0);
//        row.createCell(0).setCellValue("期号");
//        row.createCell(1).setCellValue("0");
//        row.createCell(2).setCellValue("1");
//        row.createCell(3).setCellValue("2");
//        row.createCell(4).setCellValue("3");
//        row.createCell(5).setCellValue("4");
//        row.createCell(6).setCellValue("5");
//        row.createCell(7).setCellValue("6");
//        row.createCell(8).setCellValue("7");
//        row.createCell(9).setCellValue("8");
//        row.createCell(10).setCellValue("9");
//        row.createCell(11).setCellValue("3000数之样式");
//        row.createCell(12).setCellValue("需要上传的3000数");
//        //赋值3000模板书数
//        List<Integer> anInt = get3000Int();
//        for (Integer num : anInt) {
//            int lastRowNum = sheet.getLastRowNum();//获取最后一行的行号
//            HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
//            dataRow.createCell(11).setCellValue(num);
//        }
//        //模板日期
//        HSSFRow row1 = sheet.getRow(1);
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        Calendar cal = Calendar.getInstance();
//        int year = cal.get(Calendar.YEAR);
//        int mon = cal.get(Calendar.MONTH) + 1;
//        int day = cal.get(Calendar.YEAR);
//        String format1 = "" + year +"" + mon + "" +day;
//        row1.createCell(0).setCellValue(format.format(new Date()) + "01");
//        row1.createCell(2).setCellValue(1);
//        row1.createCell(3).setCellValue(2);
//        row1.createCell(4).setCellValue(3);
//        //5.创建文件名
//        String fileName = "上传模板.xls";
//        //6.获取输出流对象
//        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
//        response.setContentType("multipart/form-data");
//        //设置请求头
//        ServletOutputStream outputStream = response.getOutputStream();
//        workbook.write(outputStream);
//        return CommonResponse.success();
//    }
//
//
//    public void print(String value,Long start,Long end){
//        log.info(value+(end-start));
//    }
//
//    private static Integer groupInt = 301; //多少组  3001
//
//    private static Integer groupRow = 300; //多少条数据 3000
//
//    private static Integer groupNum = 21; //多少组在分一组
//
//    @Autowired
//    private LyqTableService lyqTableService;
//
//    @Autowired
//    private LyqDateService lyqDateService;
//
//    private static Map<String, Object> map = new HashMap<>();
//
//    private static Map<String, Object> intByFile = new HashMap<>();

}
