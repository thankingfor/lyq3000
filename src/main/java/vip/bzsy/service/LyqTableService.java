package vip.bzsy.service;

import vip.bzsy.model.LyqTable;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author lyf
 * @since 2019-04-01
 */
public interface LyqTableService extends IService<LyqTable> {

    Integer updateToZore(String trim);

    void saveBatchByMyself(List<LyqTable> list);

    List<LyqTable> listByGroupOrderByAsc(Integer gruop);
}
