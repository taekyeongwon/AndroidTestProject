package com.tkw.kr.myapplication.component.covid

data class CovidDTO(var disasterSmsList: ArrayList<DisasterSmsList>, val rtnResult: RtnResult)
class DisasterSmsList {
    var DSSTR_SE_NM: String = ""    //"전염병"
    var CREATE_DT: String = ""      //"2022\/02\/04 15:30:29"
    var RCV_AREA_NM: String = ""    //"전라남도 무안군"            o
    var MD101_SN: Int = 0           //141407                    o
    var DSSTR_SE_ID: String = ""    //"27"
    var MODF_DT: String = ""        //"2022-02-04 15:40:00.0"
    var RCV_AREA_ID: String = ""    //"188"
    var UPDUSR_ID: String = ""      //"NDMSESB"
    var MSG_SE_CD: String = ""      //"cbs"
    var DELETE_AT: String = ""      //"N"
    var MSG_CN: String = ""         //"[무안군청]1.30 21:12~1.31 2:20\/1.31 19:00~22:00해제양간다리포차(해제면 봉대로 45,2층)방문자는 선별진료소에서 신속항원검사 바랍니다" o
    var RNUM: Int = 0               //1
    var EMRGNCY_STEP_ID: String = "" //"4372"
    var REGIST_DT: String = ""      //"2022-02-04 15:30:30.0"   o
    var REGISTER_ID: String = ""    //"NDMSESB"
    var EMRGNCY_STEP_NM: String = ""  //"안전안내"
    var confirmed_count: Int = 0 //확진자수
}
data class RtnResult(val pageSize: Int, val resultCode: Int, val resultMsg: String, val totCnt: Int)