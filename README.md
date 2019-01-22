# Bookkeeping
Приложение для конвертации валют. Изначально создавалось для тестового задания при устройстве на работу, 
поэтому UI простой. В дальнейшем расширил функционал с использованием Room, Dagger, RxJava для практики.  

## Функционал

### Сущности
1. Счет - место хранение денег (Account). Каждый счет имеет наименование и валюту.
2. Транзакция - операция по счету (Transaction). Содержит дату, сумму, валюту и комментарий. Транзакции могут быть как приходными, так и расходными.

При отображении валютных счетов и транзакций помимо основной суммы в валюте также отображается сумма в рублях. В качестве курса конвертации используется курс ЦБ на дату транзакции, для счетов - на текущий день. Курс автоматически загружается с [сайта ЦБ европы](https://www.ecb.europa.eu/stats/)

### Классический сценарий использования
1. Заводим один или несколько счетов.
2. Создаем операции по счетам в разных валютах.
3. Смотрим текущее состояние счетов.

Реализована возможность сохранения данных в облако Firebase, требуется аутентификация.
Возможность построения графика истории изменения рэйта выбранной валюты.
При запуске приложения значение в рублях для счета автоматически обновляется в соответствии со значением валюты на сегодняшний день.
Также можно удалить счет или транзакцию.

## Архитектура

### Branches
Есть две ветки dagger и mvp, в master тоже самое что и в mvp. По функционалу ветки одинаковые, однако отличаются архитектурой. В dagger ветке используется  dependency injection, присутствуют model классы, но основная логика реализована в самих классах Activity. В mvp соответственно реализована mvp архитектура и логика перенесена в презентер.

### Subcomponents
Для всех подкомпонентов использовал singleton scope, то есть их жизненный цикл совпадает с жизненным циклом activity. По сути подкомпоненты здесь нужны только для того, чтобы в разных activity создавались только те модули, которые ему нужны. 

## MIT License

Copyright (c) 2019 Offan Arkadiy Kazimirovich

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
