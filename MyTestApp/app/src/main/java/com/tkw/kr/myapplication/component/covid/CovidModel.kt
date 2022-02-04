package com.tkw.kr.myapplication.component.covid

data class CovidModel(val disasterSmsList: ArrayList<DisasterSmsList>, val rtnResult: RtnResult)
data class DisasterSmsList(val DSSTR_SE_NM: String, //"전염병"
                           val CREATE_DT: String,   //"2022\/02\/04 15:30:29"   o
                           val RCV_AREA_NM: String, //"전라남도 무안군"            o
                           val MD101_SN: Int,       //141407                    o
                           val DSSTR_SE_ID: String, //"27"
                           val MODF_DT: String,     //"2022-02-04 15:40:00.0"
                           val RCV_AREA_ID: String, //"188"
                           val UPDUSR_ID: String,   //"NDMSESB"
                           val MSG_SE_CD: String,   //"cbs"
                           val DELETE_AT: String,   //"N"
                           val MSG_CN: String,      //"[무안군청]1.30 21:12~1.31 2:20\/1.31 19:00~22:00해제양간다리포차(해제면 봉대로 45,2층)방문자는 선별진료소에서 신속항원검사 바랍니다" o
                           val RNUM: Int,           //1
                           val EMRGNCY_STEP_ID: String, //"4372"
                           val REGIST_DT: String,   //"2022-02-04 15:30:30.0"
                           val REGISTER_ID: String, //"NDMSESB"
                           val EMRGNCY_STEP_NM: String  //"안전안내"
                           )
data class RtnResult(val pageSize: Int, val resultCode: Int, val resultMsg: String, val totCnt: Int)