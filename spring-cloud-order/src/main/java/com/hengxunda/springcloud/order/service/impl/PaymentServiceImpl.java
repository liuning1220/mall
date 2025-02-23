package com.hengxunda.springcloud.order.service.impl;

import com.hengxunda.springcloud.common.exception.ServiceException;
import com.hengxunda.springcloud.order.client.AccountClient;
import com.hengxunda.springcloud.order.client.AccountRibbon;
import com.hengxunda.springcloud.order.client.InventoryClient;
import com.hengxunda.springcloud.order.client.InventoryRibbon;
import com.hengxunda.springcloud.order.dto.AccountDTO;
import com.hengxunda.springcloud.order.dto.InventoryDTO;
import com.hengxunda.springcloud.order.entity.Order;
import com.hengxunda.springcloud.order.service.PaymentService;
import com.hengxunda.springcloud.service.common.redis.RedisDistributedLock;
import com.hengxunda.springcloud.service.common.redis.RedisHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private InventoryClient inventoryClient;

    @Autowired
    private AccountRibbon accountRibbon;

    @Autowired
    private InventoryRibbon inventoryRibbon;

    @Autowired
    private RedisHelper redisHelper;

    @Override
    @Transactional
    public void makePayment(Order order) {
        final BigDecimal accountInfo = accountRibbon.findByUserId(order.getUserId());
        if (accountInfo.compareTo(order.getTotalAmount()) < 0) {
            throw new ServiceException("余额不足");
        }

        final Integer inventoryInfo = inventoryRibbon.findByProductId(order.getProductId());
        if (inventoryInfo < order.getCount()) {
            throw new ServiceException("库存不足");
        }

        // 扣除用户余额
        AccountDTO accountDTO = AccountDTO.builder().amount(order.getTotalAmount()).userId(order.getUserId()).build();
        log.info("{}", "===========执行spring cloud扣减资金==========");
        accountRibbon.payment(accountDTO);

        // 进入扣减库存
        InventoryDTO inventoryDTO = InventoryDTO.builder().count(order.getCount()).productId(order.getProductId()).build();
        log.info("{}", "===========执行spring cloud扣减库存==========");
        final int timeoutMs = 10000;
        final int expireMs = 20000;
        RedisDistributedLock lock = new RedisDistributedLock(redisHelper, "order:pay:" + order.getOrderNo(), timeoutMs, expireMs);
        try {
            if (lock.lock()) {
                log.info("lock key : {}", lock.getLockKey());
                inventoryRibbon.decrease(inventoryDTO);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }
}
