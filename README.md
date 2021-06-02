# Completeness, Purity, Invariant for Domain Models

ドメインモデルの完全性、純粋性、不変性についての実装パターン集です。
内容、コード例の題材は、Vladimir Khorikovさんの以下の記事を大いに参考にしています。

* https://enterprisecraftsmanship.com/posts/validation-and-ddd/
* https://enterprisecraftsmanship.com/posts/domain-model-purity-completeness/

また詳細を [こちら](https://scrapbox.io/kawasima/%E3%83%89%E3%83%A1%E3%82%A4%E3%83%B3%E3%83%A2%E3%83%87%E3%83%AB%E3%81%AE%E5%AE%8C%E5%85%A8%E6%80%A7%E3%81%A8%E7%B4%94%E7%B2%8B%E6%80%A7) に書いてあります。

## Completeness, Purity

ドメインモデルが完全であることと、ドメインモデルが純粋であることにはトレードオフがあり、両立が難しい場合がある。

ユーザはある企業に属していて、そのユーザのEメールアドレスを変更することを考える。
この [実装](src/main/java/net/unit8/example/completeness/ChangeUserEmailHandler.java) は、完全(ドメインロジックがドメイン層に閉じていて)かつ純粋(ドメイン層が別のレイヤーに依存していない)に実装できる。

ここで、変更するメールアドレスが「全ユーザと重複していないこと」という制約を加えるとする。これは1ユーザの集約の範囲を超えるので、重複チェックデータベースアクセス用のPortを追加してチェックする。
このとき実装の仕方として2通り考えられる。

* [純粋性を守る](src/main/java/net/unit8/example/completeness/ChangeUserUniqueEmailHandler.java) ; 完全性は失われる
* [完全性を守る](src/main/java/net/unit8/example/completeness/ChangeUserUniqueEmailByPortHandler.java) ; 純粋性は失われる


## Invariant

注文を配送状態にする処理である。
注文の配送処理時に、配送先住所と配達日時が渡され、注文が更新される。

注文によっては、制約として「日本国内配送のみ」「土日配送不可」のものがあり、この制約に抵触しないかをビジネスルールとチェックして、配送処理を行うものとする。

実装のパターンとしては、
1. 注文(Order)にバリデーションメソッドを実装し、配送処理前にそれをコールする。(実装: [Order](src/main/java/net/unit8/example/invariant/isvalid/Order.java) / [App](src/main/java/net/unit8/example/invariant/isvalid/DeliverOrderHandlerImpl.java) )
2. 注文の配送処理前にアプリケーションレイヤで、注文の配送時の制約をチェックする。 (実装: [Order](src/main/java/net/unit8/example/invariant/applayer/Order.java) / [App](src/main/java/net/unit8/example/invariant/applayer/DeliverOrderHandlerImpl.java) )
3. 配送処理の中で、ビジネスルールのチェックを同時に行う。(実装: [Order](src/main/java/net/unit8/example/invariant/tryexecute/Order.java) / [App](src/main/java/net/unit8/example/invariant/tryexecute/DeliverOrderHandlerImpl.java) )

が考えられる。ここにそれぞれのパターンを比較のために実装している。

ここで、外部のサービスを使って「郵便番号、住所が実在するものでなくてはならない」というビジネスルールを加えたとしよう。
これは、注文集約の外に依存するので、前述の完全性と純粋性が両立できないケースになる。このとき、どう実装したらよいだろうか?

完全性と純粋性が同時に満たせないとき、純粋性を守りにいくケースをよく見かけるし、[この記事](https://enterprisecraftsmanship.com/posts/domain-model-purity-completeness/) でもそれが推奨されている。
完全性が失われるデメリットの1つに、アプリケーションレイヤーでのビジネスルールのチェック漏れの懸念があるが、これはビジネスルールを満たすかチェック済みの型を分けることで防ぐことができる。
(実装: [Order](src/main/java/net/unit8/example/invariant/incompleteness/Order.java) / [App](src/main/java/net/unit8/example/invariant/incompleteness/DeliverOrderHandlerImpl.java)

