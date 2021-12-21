package com.tkw.kr.myapplication.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.tkw.kr.myapplication.core.network.error.AppError
import com.tkw.kr.myapplication.util.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers

/* 코루틴 스코프 만들고 (AAC ViewModel에서 viewModelScope 지원, 코루틴의 cancel()을 생애주기에 맞춰 관리해줌 - 디폴트는 메인스레드)
 launch(Job 반환하고 결과값 제공x) 혹은 async(Deferred 반환후 await()로 결과값 제공o)로 비동기 동작 처리(인자로 Dispatchers.Main, Default, IO 설정 가능)
 withContext(Dispatchers.Main, Default, IO)로 coroutineContext의 dispatcher 변경해서 처리 가능 - async()와 같이 결과 반환 기다린 후 다음 문장 실행

 * 참고
 launch() - join(), async() - await(), runBlocking() 결과 대기 메소드
 코루틴 스코프의 범위가 다르면 외부 코루틴 블록이 종료되어도 내부 코루틴 블록은 끝까지 실행될 수 있다.

 viewModelScope사용 시 Dispatcher는 SupervisorJob + Dispachers.Main으로 이루어져 익셉션이 단방향(부모->자식)으로만 전파됨.
 retrofit이 백그라운드에서 작업해주기 때문에 다른 디스패처에서 작업하지 않아도 된다.
 (SupervisorJob 사용하지 않는 경우 자식 코루틴에서 발생한 익셉션이 부모로 올라가 전부 종료된다.
 즉 CoroutineExceptionHandler를 걸어두어도 종료가 되어버려 핸들링이 불가능.
 네트워크 통신 및 다른 작업에서 Exception이 발생해도 프로그레스바 등 UI작업이 필요한 경우에 사용한다.)
async도 핸들링이 가능한지 확인하기!
*/
abstract class BaseViewModel: ViewModel() {
    protected val _progressFlag = SingleLiveEvent<Boolean>()
    protected val _alertFlag = SingleLiveEvent<AppError.Base>()

    val progressFlag: LiveData<Boolean> get() = _progressFlag
    val alertFlag: LiveData<AppError.Base> get() = _alertFlag

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        throwable.printStackTrace()
    }

    protected val ioDispatchers = Dispatchers.IO + coroutineExceptionHandler
    protected val uiDispatchers = Dispatchers.Main + coroutineExceptionHandler
}