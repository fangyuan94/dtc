package com.fc.dtc.cache;


import com.fc.dtc.bean.DisctionaryBean;
import com.fc.dtc.constant.CacheConstant;
import com.fc.dtc.exception.LockExitException;
import com.fc.dtc.exception.TranslateException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author fangyuan
 * 本地缓存
 */
@Getter
public class LocalDisctionaryTranslate  extends AbstractDisctionaryTranslate  {

    public   Map<String, Map<String,String>> localCacheData ;

    protected Map<String, TreeSet<DisctionaryBean>> localCacheDataType;

    //锁
    private final AtomicBoolean flag = new AtomicBoolean(true);

    public LocalDisctionaryTranslate(JdbcTemplate  jdbcTemplate,DisctionaryJDBCActuator disctionaryJDBCActuator){

        this.localCacheData = new HashMap<>();
        this.jdbcTemplate=jdbcTemplate;
        this.disctionaryJDBCActuator= disctionaryJDBCActuator;
        super.init();
    }

    @Override
    public String get(String cacheKey,String filedKey) {
        return localCacheData.get(cacheKey).get(filedKey);
    }

    @Override
    public TreeSet<DisctionaryBean> getDictionaryByType(String type) {

        return localCacheDataType.get(type);
    }

    @Override
    public void dtcRefresh() {

        if(flag.compareAndSet(true,false)){
            //重新执行init
            super.init();
        }else{
            throw new LockExitException("字典正常重新缓存中请等待。。。。");
        }

    }

    @Override
    public void wirteToCache() {

        if (cacheData.size() > 0) {
            // 将代码code保存到缓存中
            this.localCacheData.put(CacheConstant.DMZ_DMMC, this.cacheData);
        }

        if (cacheData1.size() > 0) {
            this.localCacheData.put(CacheConstant.DMMC_DMZ, this.cacheData1);
        }

        if (cacheData3.size() > 0) {
            this.localCacheDataType = new HashMap<>(this.cacheData3.size());
            localCacheDataType.putAll(this.cacheData3);
        }

    }

}
