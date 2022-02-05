# GitLabInfo_Bot

Данный бот позволяет получать `mergeRequests` и `issues` из проектов `Gitlab` посредством явного запроса (команда/нажатие кнопки) и нотификации. 

Ориентирован на использование в нескольких чатах (основной и проектные):
- в основной чат бот отсылает данные по всем проектам (при запросе командой, кнопкой или нотификацией)
- в чат какого-либо проекта только то, что относится к этому проекту

##Возможности

###1. Нотификации:
1. Отправка `mergeRequests` выведенных на `review` (label `state::review`) и последующее редактирование отправленных уведомлений при изменении количества лайков у конкретного `mergeRequest`. Отправленный реквест стирается из истории, если с момента его отправки прошло >= 5 дней
2. Отправка ошибок в чат поддержки (helpChat). Задается в `telegram.properties`

###2. Команды:

> Для команд `2-5` предусмотрено меню, которое вызывается командой `1`

1. `/menu@GitLabInfo_Bot` - вызывает меню, которое содержит кнопки отвечающие за команды 2-5
2. `/getAllReview_MR` - получить `mergeRequests` на `review` (label `state::review`)
3. `/getAllDoing_MR` - получить `mergeRequests` в работе (label `state::doing`)
4. `/getAllReview_I` - получить `issues` на `review`
5. `/getAllDoing_I` - получить `issues` в работе 
6. `/sendToAll` - отправка сообщения, текст которого пишется после этой команды в том же сообщении, во все доверенные чаты.
    > Замечание: работает только в чате поддержки (`tg.helpChat`). Также запрашивается подтверждение
7. `/shutDown` - отключение бота.
    > Замечание: работает только в чате поддержки (`tg.helpChat`). Также запрашивается подтверждение
                 
###3. Клавиатуры:
1. Menu (`/menu@GitLabInfo_Bot`). 
   Содержит кнопки: 🔴 Review_MR (`/getAllReview_MR`), 🟢 Doing_MR (`/getAllDoing_MR`), 🔴 Review_I (`/getAllReview_I`), 🟢 Doing_I (`/getAllDoing_I`)
2. Popup (подтверждение выполнения `/sendToAll`/`/shutDown`). 
   Содержит кнопки: `Да`, `Нет`
3. Notification popup (уведомление о реквестах с label `state::review`, позволяющее дополнить свой текст списками `Approved` и `Commented`). Доступно для основного чата
   Содержит кнопки: `Approve`, `Comment`.
   Списки изменяются по нажатию на кнопки.
   > Замечание: кнопки присутствуют в уведомлении, если значение параметра  `ntf.reviewWithButtons.forMainChat` в notification.properties равняется `true`

##Настройка

Необходимо создать бота в telegram с помощью @BotFather и задать значения нижеуказанным properties.

> Обязательные разделы (не заполнены по умолчанию): 1, 2, 4

###1. bot.properties
- `bot.username` - имя вашего бота 
- `bot.token` - токен вашего бота

###2. gitlab.properties
- `gl.baseUrl` - url вашего Gitlab
- `gl.privateToken` - персональный токен вашего Gitlab (User Settings -> Access Tokens -> Personal Access Tokens)
- `gl.projectsGroupId` - id группы в Gitlab с вашими проектами (возможна передача нескольких групп разделенных ", ")
- `gl.groupWithSubGroups=true` - считывать ли проекты из подгрупп группы `gl.projectsGroupId`

За какое кол-во времени считывать данные из Gitlab (по умолчанию считываются `mergeRequests`/`issues`, которые `updated` за последнюю неделю):

- `gl.mR_and_I_lastMinutes=10080` (10080 - 7 дней)

###3. notification.properties

Будние дни: 
- `ntf.reviewMR_lastMinutes=20` (считываются данные, которые `updated` за последние 20 минут):
- `ntf.reviewI_lastMinutes=20`

Первое уведомление после выходных:
- `ntf.reviewMR_afterWeekend=4320` (4320 минут - 3 дня)
- `ntf.reviewI_afterWeekend=4320`

- `ntf.periodForReview_MR_I_cron=0 0/20 9-20 ? * MON,TUE,WED,THU,FRI *` (каждую 30-ую минуту с 09:00 до 20:00 с понедельника по пятницу)
- `ntf.periodForReview_afterWeekend_MR_I_cron=0 0 9 ? * MON *` (в 09:00 в понедельник)

- `ntf.enabledMainProjects=false` (отправлять/не отправлять уведомления в чаты проектов)
- `ntf.reviewWithButtons.forMainChat=true` (см.раздел `Клавиатуры` п.3)

###4. telegram.properties
- `tg.helpChat` - id чата в который будут отправляться ошибки бота (сообщение + часть стектрейса)
- `tg.mainChatId` -  id основного чата

###Необязательные параметры:

> Заполняются в случае, если бот добавлен в чат конкретного(-ых) проекта(-ов). 
> Также, при добавлении бота в проектные чаты, необходимо установить значение `true` параметру `ntf.enabledMainProjects` в notification.properties

1. telegram.properties:

    - `tg.project_1_Id` - id чата проекта 1 в Telegram
    - `tg.project_2_Id` - id чата проекта 2 в Telegram
    - `tg.project_3_Id` - ...
    - `tg.project_4_Id` - ...
    - `tg.project_5_Id` - ...
    - `tg.project_6_Id` - ...

2. gitlab.properties:

    - `gl.project_1_Id` - id проекта 1 в Gitlab
    - `gl.project_2_Id` - id проекта 2 в Gitlab
    - `gl.project_3_Id` - ...
    - `gl.project_4_Id` - ...
    - `gl.project_5_Id` - ...
    - `gl.project_6_Id` - ...

Необходимо оставить только заполненные `tg.project_<номер проекта>_Id`/`gl.project_<номер проекта>_Id`. Если проектов больше, то дополнить список. 
Также требуется соответственно данному списку уменьшить/дополнить `telegramBot.enums.MainProjects`