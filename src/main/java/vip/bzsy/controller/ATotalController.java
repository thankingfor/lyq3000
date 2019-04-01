package vip.bzsy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import vip.bzsy.common.CommonResponse;
import vip.bzsy.model.LyqTable;
import vip.bzsy.model.Type2Vo;
import vip.bzsy.service.LyqTableService;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author lyf
 * @create 2019-04-01 15:24
 */
@Slf4j
@Controller
public class ATotalController {

    @ResponseBody
    @RequestMapping(value = "/test")
    public CommonResponse test() {
        return CommonResponse.success("测试成功");
    }

    @RequestMapping(value = {"/", "/index"})
    public String index(Model model) {
        return "index";
    }

    /**
     * 按钮二 获取3000个数据
     * 1.获取第0组
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copy")
    public CommonResponse copy(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 第一项任务
         */
        List<LyqTable> list0 = copyStart();//给第0组赋值
        List<LyqTable> listMax = new LinkedList<>();//存放最大值
        Map<String, List<LyqTable>> map = new HashMap<>(); //list都是按照value降序排序的，未修改过的
        map.put("0", list0);
        //开始修改0-3000组的key
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> tables = copySort(list0, group);//本组的降序的tables
            listMax.add(tables.get(0));//把最大的值存放
            map.put(group + "", tables);//放入map中
            list0 = tables;//把list0改成下一组的方便循环
        }
        /**
         * 第二个任务 700个
         * 按照lyq_seq进行排序
         */
        List<Type2Vo> type2VoListType = new LinkedList<>();
        Map<String, List<LyqTable>> listMap = new HashMap<>();
        Integer groupType2 = 1;
        for (int group = 1; group < groupInt; group++) {
            List<LyqTable> listgroup = lyqTableService.list(new QueryWrapper<LyqTable>()
                    .eq("lyq_group", group).orderByAsc("lyq_seq"));
            listMap.put(group + "", listgroup);
            if (group % groupNum == 0) {
                List<Type2Vo> type2VoList = sortType2(listMap, groupType2);
                type2VoListType.addAll(type2VoList);
                listMap.clear();
                groupType2++;
            }
        }
        //type2VoListType最终排序
        type2VoListType.sort((x,y)->y.getValue()-x.getValue());
        //进行下载操作
        copyDownMax(response, listMax,type2VoListType);//操作list进行下载  日期号  组  key value
        return CommonResponse.success();
    }

    /**
     * 返回前五条数据
     * @param listMap
     * @param group
     * @return
     */
    public List<Type2Vo> sortType2(Map<String, List<LyqTable>> listMap, Integer group) {
        Map<Integer, Integer> map = new TreeMap<>();//默认升序
        for (String key : listMap.keySet()) {
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
        // 排序
        // 降序排序比较器
        Comparator<Map.Entry<Integer, Integer>> keyComparator = new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {

                return o2.getValue() - o1.getValue();
            }
        };
        List<Map.Entry<Integer, Integer>> lists = new ArrayList<>(map.entrySet());
        Collections.sort(lists, keyComparator);
        log.info(lists.toString());
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

    public void copyDownMax(HttpServletResponse response, List<LyqTable> listMax,
                            List<Type2Vo> type2VoListType) throws Exception {
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
        for (int i = 0;i < type2VoListType.size();i++){
            HSSFRow dataRow = sheet.getRow(i + 1);
            dataRow.createCell(4).setCellValue("第" + type2VoListType.get(i).getGroup() + "组");
            dataRow.createCell(5).setCellValue(type2VoListType.get(i).getSeq());
            dataRow.createCell(6).setCellValue(type2VoListType.get(i).getValue());
        }
        //5.创建文件名
        String fileName = "期号加.xls";
        //6.获取输出流对象
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "utf-8"));
        response.setContentType("multipart/form-data");
        //设置请求头
        ServletOutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
    }

    /**
     * 对每一组的更新
     * 需要知道前一组的情况
     *
     * @param copyList 前一组的list
     * @return 这一组的list
     */
    public List<LyqTable> copySort(List<LyqTable> copyList, Integer gruop) {
        List<LyqTable> listgroupbySeq = lyqTableService.list(new QueryWrapper<LyqTable>()
                .eq("lyq_group", gruop).orderByAsc("seq"));
        //把key 复给 下一组 带上0组的seq
        for (int i = 0; i < groupRow; i++) {
            if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }
            listgroupbySeq.get(i).setLyqKey(copyList.get(i).getLyqKey()); //把上一组的key给这一组
            listgroupbySeq.get(i).setLyqSeq(copyList.get(i).getLyqSeq()); //把第零组的序列付给每一组
        }
        //批量更新
        lyqTableService.updateBatchById(listgroupbySeq);
        //按照降序查询出本组所有的list
        List<LyqTable> listgroup = lyqTableService.list(new QueryWrapper<LyqTable>()
                .eq("lyq_group", gruop).orderByDesc("lyq_value"));
        return listgroup;
    }

    /**
     * copy的初始化操作
     */
    public List<LyqTable> copyStart() {
        List<LyqTable> listgroupbySeq = lyqTableService.list(new QueryWrapper<LyqTable>()
                .eq("lyq_group", 0).orderByAsc("seq"));
        //模拟Excel获取3000个数据
        List<Integer> anInt = get3000Int();
        for (int i = 0; i < groupRow; i++) {
            if (listgroupbySeq.get(i).getSeq() != i + 1) {
                throw new RuntimeException("顺序不一致");
            }
            listgroupbySeq.get(i).setLyqKey(anInt.get(i)); //把上一组的key给这一组
        }
        //批量更新
        lyqTableService.updateBatchById(listgroupbySeq);
        List<LyqTable> lyq_gruop0 = lyqTableService.list(new QueryWrapper<LyqTable>()
                .eq("lyq_group", 0).orderByDesc("lyq_value"));
        return lyq_gruop0;
    }


    /**
     * 按钮一 通过 几位数来进行操作
     * key相同的情况下 value为0
     * 其他的value+1
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/replace")
    public CommonResponse replace(String ids) {
        //把所有的数据改为0
        Integer toZore = lyqTableService.updateToZore(ids.trim());
        return CommonResponse.success(toZore);
    }


    /**
     * 初始化数据
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/init")
    public CommonResponse init() {
        //获取3000条数据
        boolean b = initInsert();
        if (!b) {
            return CommonResponse.fail("插入失败");
        }
        return CommonResponse.success();
    }

    public boolean initInsert() {
        //删除表中的所有数据
        boolean remove = lyqTableService.remove(null);
        //添加第0-3000组
        List<LyqTable> list = new LinkedList<>();
        for (int i = 0; i < groupInt; i++) {
            list.addAll(getInit3000(i));
        }
        lyqTableService.saveBatch(list);
        return remove;
    }

    public List<Integer> get3000Int() {
        Random random = new Random();
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < groupRow; i++) {
            list.add(random.nextInt(10));
        }
        return list;
    }

    public List<LyqTable> getInit3000(Integer group) {
        log.info("第" + group + "组");
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

    private static Integer groupInt = 31; //多少组  3001

    private static Integer groupRow = 30; //多少条数据 3000

    private static Integer groupNum = 13; //多少组在分一组

    @Autowired
    private LyqTableService lyqTableService;

}
