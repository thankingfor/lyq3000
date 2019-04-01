package vip.bzsy.mapper;

import org.apache.ibatis.annotations.Param;
import vip.bzsy.model.LyqTable;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-04-01
 */
public interface LyqTableMapper extends BaseMapper<LyqTable> {

    Integer updateToZore(@Param("ids") Integer[] ids);

    Integer updateToAdd(@Param("ids") Integer[] ids);
}
