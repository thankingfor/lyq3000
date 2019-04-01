package vip.bzsy.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import vip.bzsy.model.LyqTable;
import vip.bzsy.mapper.LyqTableMapper;
import vip.bzsy.service.LyqTableService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-04-01
 */
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
        Integer row1 = lyqTableMapper.updateToZore(ids);
        Integer row2 = lyqTableMapper.updateToAdd(ids);
        return row1+row2;
    }

    @SuppressWarnings("all")
    @Autowired
    private LyqTableMapper lyqTableMapper;
}
