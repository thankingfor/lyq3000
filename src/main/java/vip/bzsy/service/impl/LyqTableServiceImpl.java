package vip.bzsy.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vip.bzsy.model.LyqTable;
import vip.bzsy.mapper.LyqTableMapper;
import vip.bzsy.service.LyqTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-04-01
 */
@Slf4j
@Service
public class LyqTableServiceImpl extends ServiceImpl<LyqTableMapper, LyqTable> implements LyqTableService {

    @Override
    @Transactional
    public Integer updateToZore(String trim) {
        //变为0 array
        String[] split = trim.split(",");
        Integer[] ids = new Integer[split.length];
        for (int i = 0; i < split.length; i++) {
            ids[i] = Integer.valueOf(split[i].trim());
        }
        long time1 = System.currentTimeMillis();
        Integer row1 = lyqTableMapper.updateToZore(ids);
        log.info("开始操作"+(time1));
        long time2 = System.currentTimeMillis();
        log.info("归零操作"+(time2-time1));
        Integer row2 = lyqTableMapper.updateToAdd(ids);
        long time3 = System.currentTimeMillis();
        log.info("加加操作"+(time3-time2));
        return row1+row2;
    }

    @Override
    public void saveBatchByMyself(List<LyqTable> list) {
        lyqTableMapper.saveBatchByMyself(list);
    }

    @Override
    public List<LyqTable> listByGroupOrderByAsc(Integer gruop) {
        return lyqTableMapper.listByGroupOrderByAsc(gruop);
    }

    @SuppressWarnings("all")
    @Autowired
    private LyqTableMapper lyqTableMapper;
}
