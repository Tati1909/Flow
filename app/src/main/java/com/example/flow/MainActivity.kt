package com.example.flow

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * На простом примере разберём, как можно использовать Flow в качестве источника данных и
уйти от RxJava в своем проекте.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFlow()
        startFlow()
    }

    /**
     * Метод getFlow возвращает нам поток данных, который последовательно испускает
     * числовые значения от 0 до 10.
     *
     * Мы просто запускаем цикл от 0 до 10 ((0..10).forEach), ставим задержку на полсекунды и
     * испускаем новое число emit(it). В конце мы применяем оператор flowOn (flowOn(Dispatchers.Default)),
     * который говорит нам, что поток будет выполняться отдельно.
     */
    private fun getFlow(): Flow<Int> = flow {
        Log.d(TAG, "Start flow")
        (0..10).forEach { int ->
            delay(500)
            Log.d(TAG, "Emitting $int")
            emit(int)
        }
    }.flowOn(Dispatchers.Default)
    /**
     * Dispatchers во Flow можно сравнить с Schedulers в rx, а flowOn() с subscribeOn().
     */

    /**
     * Мы вешаем слушатель нажатий на кнопку и запускаем асинхронную работу
     * (CoroutineScope(Dispatchers.Main).launch), которая будет возвращать результат в главный
    поток приложения. Flow запускается через вызов метода collect (собрать данные) и
    возвращает нам последовательно числа от 0 до 10:
     */
    private fun startFlow() {
        findViewById<Button>(R.id.button).setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
//import kotlinx.coroutines.flow.collect
                getFlow().collect {
                    Log.d(TAG, it.toString())
                }
            }
        }
    }

    companion object {
        private const val TAG = "###"
    }
}