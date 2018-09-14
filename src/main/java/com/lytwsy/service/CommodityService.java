package com.lytwsy.service;

import com.lytwsy.dao.CommodityDao;
import com.lytwsy.dao.MemberDao;
import com.lytwsy.dao.TransactionRecordDao;
import com.lytwsy.domain.Commodity;
import com.lytwsy.domain.Member;
import com.lytwsy.domain.TransactionRecord;
import com.lytwsy.exception.BalanceNotEnoughException;
import com.lytwsy.exception.CommodityNumberNotEnoughException;
import com.lytwsy.exception.IdNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Code that Changed the World
 * Pro said
 * Created by Pro on 2017-12-17.
 */
@Service
public class CommodityService {

    @Resource
    private CommodityDao commodityDao;
    @Resource
    private MemberDao memberDao;
    @Resource
    private TransactionRecordDao transactionRecordDao;

    public Commodity add(Commodity commodity) {
        return commodityDao.save(commodity);
    }

    @Transactional
    public void purchaseCommodity(String memberId, String commodityId, boolean balance) {
        Member member = memberDao.findMemberById(memberId);
        Commodity commodity = commodityDao.findCommodityById(commodityId);
        //确保存在两个id的实体
        if (member != null && commodity != null) {
            //确保商品数量充足
            if (commodity.getCommodityNumber() - 1 < 0)
                throw new CommodityNumberNotEnoughException();
            //是否余额支付
            if (balance) {
                //确保余额足够支付
                if ((int) (member.getBalance() - commodity.getCommodityPrice()) >= 0) {
                    //重新设定余额
                    member.setBalance(member.getBalance() - commodity.getCommodityPrice());
                    //商品数量少1
                    commodity.setCommodityNumber(commodity.getCommodityNumber() - 1);
                    //商品积分增加
                    member.setMemberIntegral(member.getMemberIntegral()+commodity.getCommodityIntegral());
                    memberDao.save(member);
                    commodityDao.save(commodity);
                    TransactionRecord transactionRecord = new TransactionRecord();
                    transactionRecord.setMember(member);
                    transactionRecord.setCommodity(commodity);
                    transactionRecordDao.save(transactionRecord);
                    return;
                }
                throw new BalanceNotEnoughException();
            } else {
                commodity.setCommodityNumber(commodity.getCommodityNumber() - 1);
                member.setMemberIntegral(member.getMemberIntegral()+commodity.getCommodityIntegral());
                memberDao.save(member);
                commodityDao.save(commodity);
                TransactionRecord transactionRecord = new TransactionRecord();
                transactionRecord.setMember(member);
                transactionRecord.setCommodity(commodity);
                transactionRecordDao.save(transactionRecord);
                return;
            }

        }
        throw new IdNotFoundException();
    }


    public void updateMemberGrade(Commodity commodity) {
        if (commodityDao.findById(commodity.getId())!=null){
            commodityDao.save(commodity);
            return;
        }
        throw new RuntimeException("Commodity中不存在当前的id:"+commodity.getId());
    }

    public Page<Commodity> findAll(Pageable pageable) {
        return commodityDao.findAll(pageable);
    }
}
