package com.example.flow

import android.os.Bundle
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class SearchActivity : AppCompatActivity() {

    private val job: Job = Job()

    /**
     * MutableStateFlow отвечает за передачу вводимых символов в строке поиска — это поток
    данных, на которые мы подписываемся;
     */
    private val queryStateFlow = MutableStateFlow("")

    /**
     * searchView и textView: элементы интерфейса
     */
    private lateinit var searchView: SearchView
    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setUpSearchStateFlow()
    }

    /**
     * Job отвечает за корректное завершение корутин при закрытии экрана;
     */
    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }

    private fun setUpSearchStateFlow() {
        searchView = findViewById(R.id.search_view)
        textView = findViewById(R.id.result_text_view)

        /**
         * подписываемся на наш поток и получаем его обновленное состояние:
         * Debounce: это оператор, который принимает Long в качестве аргумента. Аргумент является
        периодом (в миллисекундах).
         * Filter: фильтрует пустые строки. Если пользователь ничего не ввел или все удалил, то и
        отправлять запрос нет смысла;
         * DistinctUntilChanged позволяет избегать дублирующие запросы.
         * FlatMapLatest возвращает в поток только самый последний запрос и игнорирует более ранние.
         */
        CoroutineScope(Dispatchers.Main + job).launch {
            queryStateFlow
                .debounce(500)
                .filter { query ->
                    if (query.isEmpty()) {
                        textView.text = ""
                        return@filter false
                    } else {
                        return@filter true
                    }
                }
                .distinctUntilChanged()
                .flatMapLatest { query ->
                    dataFromNetwork(query)
                        .catch {
                            emit("")
                        }
                }
                //import kotlinx.coroutines.flow.collect
                .collect { result -> textView.text = result }
        }

        searchView.setOnQueryTextListener(
            object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    query?.let { queryStateFlow.value = it }
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    queryStateFlow.value = newText
                    return true
                }
            })
    }

    //Имитируем загрузку данных по результатам ввода
    private fun dataFromNetwork(query: String): Flow<String> {
        return flow {
            delay(2000)
            emit(query)
        }
    }
}