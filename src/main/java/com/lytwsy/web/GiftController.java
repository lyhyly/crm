package com.lytwsy.web;

import com.lytwsy.domain.Gift;
import com.lytwsy.exception.GiftNumberNotEnoughException;
import com.lytwsy.exception.IdNotFoundException;
import com.lytwsy.exception.IntegralNotEnoughException;
import com.lytwsy.service.GiftService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Code that Changed the World
 * Pro said
 * Created by Pro on 2017-12-18.
 */
@Controller
public class GiftController {

    @Resource
    private GiftService giftService;

    @GetMapping("/setGift")
    public String getSetGiftView(){
        return "setGift";
    }

    @PostMapping("/setGift")
    @ResponseBody
    public String setGift(Gift gift){
        try{
            giftService.add(gift);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @PostMapping("/queryGift")
    @ResponseBody
    public Page<Gift> queryGift(Integer currentPage){
        return giftService.findGifts(currentPage);
    }


    @GetMapping("/modifyGiftNumber")
    public String getModifyGiftNumberView(){
        return "modifyGiftNumber";
    }


    @PostMapping("/modifyGiftNumber")
    @ResponseBody
    public String modifyGiftNumber(Integer giftNumber, Integer giftId){
        try{
            if (giftNumber == null || giftNumber <= 0 || giftId == null)
                return "error";
            giftService.modifyGiftNumber(giftNumber, giftId);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @PostMapping("/deleteGift")
    @ResponseBody
    public String deleteGift(Integer giftId){
        try{
            if (giftId == null)
                return "error";
            giftService.deleteGift(giftId);
        } catch (Exception e){
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    @GetMapping("/integralExchange")
    public String getIntegralExchangeView(Map<String, Object> model){
        model.put("gifts", giftService.findAll());
        return "/integralExchange";
    }

    @PostMapping("/integralExchange")
    @ResponseBody
    public String integralExchange(String memberId, Integer giftId) {
        try {
            if (giftId == null || memberId == null || "".equals(memberId)) {
                return "输入不正确！";
            }
            giftService.integralExchange(memberId, giftId);
        } catch (IntegralNotEnoughException e) {
            e.printStackTrace();
            return "会员积分不足";
        } catch (IdNotFoundException e) {
            e.printStackTrace();
            return "会员账号不存在";
        } catch (GiftNumberNotEnoughException e) {
            e.printStackTrace();
            return "礼品已经下架";
        }
        return "success";
    }
}
