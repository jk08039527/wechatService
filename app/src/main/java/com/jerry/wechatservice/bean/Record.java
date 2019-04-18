package com.jerry.wechatservice.bean;

import java.util.List;

/**
 * @author Jerry
 * @createDate 2019/4/10
 * @copyright www.aniu.tv
 * @description 一条记录
 */
public class Record {

    /**
     * log_id : 1324745721922816426
     * direction : 0
     * words_result_num : 1
     * words_result : [{"vertexes_location":[{"y":46,"x":35},{"y":46,"x":132},{"y":85,"x":132},{"y":85,"x":35}],"chars":[{"char":"s","location":{"width":21,"top":53,"left":45,"height":29}},{"char":"a","location":{"width":33,"top":53,"left":60,"height":29}},{"char":"a","location":{"width":26,"top":53,"left":89,"height":29}}],"min_finegrained_vertexes_location":[{"y":45,"x":34},{"y":46,"x":131},{"y":84,"x":131},{"y":84,"x":34}],"finegrained_vertexes_location":[{"y":46,"x":35},{"y":46,"x":63},{"y":46,"x":91},{"y":46,"x":120},{"y":46,"x":132},{"y":61,"x":132},{"y":75,"x":132},{"y":85,"x":132},{"y":85,"x":103},{"y":85,"x":75},{"y":85,"x":46},{"y":85,"x":35},{"y":71,"x":35},{"y":57,"x":35}],"location":{"width":98,"top":46,"left":35,"height":41},"words":"saa"}]
     */

    private List<WordsResultBean> words_result;

    public List<WordsResultBean> getWords_result() {
        return words_result;
    }

    public void setWords_result(List<WordsResultBean> words_result) {
        this.words_result = words_result;
    }

    public static class WordsResultBean {
        /**
         * vertexes_location : [{"y":46,"x":35},{"y":46,"x":132},{"y":85,"x":132},{"y":85,"x":35}]
         * chars : [{"char":"s","location":{"width":21,"top":53,"left":45,"height":29}},{"char":"a","location":{"width":33,"top":53,"left":60,"height":29}},{"char":"a","location":{"width":26,"top":53,"left":89,"height":29}}]
         * min_finegrained_vertexes_location : [{"y":45,"x":34},{"y":46,"x":131},{"y":84,"x":131},{"y":84,"x":34}]
         * finegrained_vertexes_location : [{"y":46,"x":35},{"y":46,"x":63},{"y":46,"x":91},{"y":46,"x":120},{"y":46,"x":132},{"y":61,"x":132},{"y":75,"x":132},{"y":85,"x":132},{"y":85,"x":103},{"y":85,"x":75},{"y":85,"x":46},{"y":85,"x":35},{"y":71,"x":35},{"y":57,"x":35}]
         * location : {"width":98,"top":46,"left":35,"height":41}
         * words : saa
         */

        private String words;

        public String getWords() {
            return words;
        }

        public void setWords(String words) {
            this.words = words;
        }
    }
}
