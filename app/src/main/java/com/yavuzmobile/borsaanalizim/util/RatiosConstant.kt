package com.yavuzmobile.borsaanalizim.util

object RatiosConstant {
    const val LABEL_MARKET_BOOK_AND_BOOK_VALUE = "PD / DD"
    const val EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE = "PD/DD oranı için sınır değer 1 olarak kabul edilir. Bu, şirketin öz sermayesiyle eşit şekilde değerlendiğini gösterir. Bu oranın 1’den yüksek olması, şirket hisselerinin piyasada olması gerekenden çok daha yüksek fiyatlandığı algısını oluşturabilir. Ancak bu her zaman o hissenin pahalı olduğunu veya alınmaması gerektiğini göstermez."

    const val LABEL_PRICE_AND_EARNING = "F / K"
    const val EXPLANATION_PRICE_AND_EARNING = "Yatırımcılar F/K oranı sayesinde bir şirketin hisse fiyatıyla hisse başına düşen kârını karşılaştırarak şirketin elde ettiği kârına oranla fiyatının olması gerekenden fazla değerlenip değerlenmediğini ölçerler. "

    const val LABEL_COMPANY_VALUE_AND_EBITDA = "FD / FAVÖK"
    const val EXPLANATION_COMPANY_VALUE_AND_EBITDA = "FD/FAVÖK oranı, bir şirketin değerinin, şirketin operasyonel kârlılığına göre ne kadar yüksek veya düşük olduğunu gösterir. Bu oran, genellikle şirketler arası karşılaştırmalar yapmak ve bir şirketin değerlemesinin, sektör ortalamasına veya benzer şirketlere göre ne kadar makul olduğunu değerlendirmek için kullanılır."

    const val LABEL_MARKET_VALUE_AND_OPERATION_PROFIT = "PD / NFK"
    const val EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT = "Şirketin piyasa değerinin net faaliyet karına oranını verir."

    const val LABEL_COMPANY_VALUE_AND_NET_SALES = "FD / NS"
    const val EXPLANATION_COMPANY_VALUE_AND_NET_SALES = "Şirketin değerinin net satışlara oranını verir. Bu oranın düşük olması büyüme potansiyelini gösterir."

    const val LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE = "NFK / PD"
    const val EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE = "Şirket piyasa değerinin yüzde kaçı kadar ana işinden kar elde edebiliyor durumunu gösteriyor."
}