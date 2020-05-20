package com.baek.app.searchbook.client;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 더미 카프카 클라이언트
 */
@Slf4j
@Component
public class KafkaClient {
    private final Map<String, BlockingQueue<String>> queueContainer = new ConcurrentHashMap<>();

    public void produce(String topic, Object value) {
        try {
            getQueue(topic).put(new Gson().toJson(value));
        } catch (Exception e) {
            log.error("", e);
        }
    }

    public <T> List<T> consume(String topic, String group, int count, Class<T> t) {
        try {
            List<T> l = new ArrayList<>();
            BlockingQueue<String> queue = getQueue(topic);
            for (int i = 0; i < count; i++) {
                String value = queue.poll();
                if (value != null) {
                    l.add(new Gson().fromJson(value, t));
                }
            }
            return l;
        } catch (Exception e) {
            log.error("", e);
            return new ArrayList<>();
        }
    }

    // 실제로는 외부 이벤트버스 등을 활용하게 될 것 같기 때문에 여기서는 단순하게 구현하였습니다.
    private BlockingQueue<String> getQueue(String topic) {
        if (queueContainer.get(topic) == null) {
            synchronized (KafkaClient.class) {
                if (queueContainer.get(topic) == null) {
                    queueContainer.put(topic, new LinkedBlockingQueue<>());
                }
            }
        }
        return queueContainer.get(topic);
    }

}
