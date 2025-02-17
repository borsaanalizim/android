package com.yavuzmobile.borsaanalizim.util

object RatiosConstant {
    const val LABEL_MARKET_BOOK_AND_BOOK_VALUE = "PD / DD"
    const val EXPLANATION_MARKET_BOOK_AND_BOOK_VALUE = "PD/DD oranı için sınır değer 1 olarak kabul edilir. Bu, şirketin öz sermayesiyle eşit şekilde değerlendiğini gösterir. Bu oranın 1’den yüksek olması, şirket hisselerinin piyasada olması gerekenden çok daha yüksek fiyatlandığı algısını oluşturabilir. Ancak bu her zaman o hissenin pahalı olduğunu veya alınmaması gerektiğini göstermez."

    const val LABEL_PRICE_AND_EARNING = "F / K"
    const val EXPLANATION_PRICE_AND_EARNING = "Yatırımcılar F/K oranı sayesinde bir şirketin hisse fiyatıyla hisse başına düşen kârını karşılaştırarak şirketin elde ettiği kârına oranla fiyatının olması gerekenden fazla değerlenip değerlenmediğini ölçerler. "

    const val LABEL_COMPANY_VALUE_AND_EBITDA = "FD / FAVÖK"
    const val EXPLANATION_COMPANY_VALUE_AND_EBITDA = "FD/FAVÖK oranı, bir şirketin değerinin, şirketin operasyonel kârlılığına göre ne kadar yüksek veya düşük olduğunu gösterir. Bu oran, genellikle şirketler arası karşılaştırmalar yapmak ve bir şirketin değerlemesinin, sektör ortalamasına veya benzer şirketlere göre ne kadar makul olduğunu değerlendirmek için kullanılır."

    const val LABEL_MARKET_VALUE_AND_OPERATION_PROFIT = "PD / NFK"
    const val EXPLANATION_MARKET_VALUE_AND_OPERATION_PROFIT = "Şirketin piyasa değerinin, faaliyet kârına göre ne kadar yüksek veya düşük olduğunu gösterir. Yatırımcıların, şirketin faaliyetlerinden elde ettiği kâra ne kadar ödediğini anlamasına yardımcı olur."

    const val LABEL_COMPANY_VALUE_AND_NET_SALES = "FD / NS"
    const val EXPLANATION_COMPANY_VALUE_AND_NET_SALES = "Şirketin satışlarına kıyasla değerlemesini gösterir. Bu oran, şirketin toplam satış hacmine göre ne kadar değerli olduğunu ifade eder."

    const val LABEL_NET_OPERATING_PROFIT_AND_MARKET_VALUE = "NFK / PD"
    const val EXPLANATION_NET_OPERATING_PROFIT_AND_MARKET_VALUE = "Şirket piyasa değerinin yüzde kaçı kadar ana işinden kar elde edebiliyor durumunu gösteriyor."

    const val LABEL_NET_REVENUE_GROWTH_RATE_VALUE = "RGR"
    const val EXPLANATION_NET_REVENUE_GROWTH_RATE_VALUE = "Net Satış Büyüme Oranı"

    const val LABEL_EBITDA_GROWTH_RATE_VALUE = "EGR"
    const val EXPLANATION_EBITDA_GROWTH_RATE_VALUE = "FAVÖK Büyüme Oranı"

    const val LABEL_NET_PROFIT_GROWTH_RATE_VALUE = "PGR"
    const val EXPLANATION_NET_PROFIT_GROWTH_RATE_VALUE = "Net Kâr Büyüme Oranı"

    const val LABEL_NET_OPERATING_MARGIN_VALUE = "OPM"
    const val EXPLANATION_NET_OPERATING_MARGIN_VALUE = "Net Faaliyet Kar Marjı"

    const val LABEL_RETURN_ON_EQUITY_VALUE = "ROE"
    const val EXPLANATION_RETURN_ON_EQUITY_VALUE = "Özsermaye Karlılığı"

    const val LABEL_CURRENT_RATE_VALUE = "CR"
    const val EXPLANATION_CURRENT_RATE_VALUE = "Cari Oran" // Dönen Varlıklar / Kısa Vadeli Yükümlülükler
    const val EXTENDED_EXPLANATION_CURRENT_RATE = "Şirketin kısa vadeli yükümlülüklerini karşılayabilme kapasitesini gösterir."

    const val LABEL_ACID_TEST_RATE_VALUE = "ATR"
    const val EXPLANATION_ACID_TEST_RATE_VALUE = "Likidite Oranı" // (Dönen Varlıklar - Stoklar) / Kısa Vadeli Yükümlülükler
    const val EXTENDED_EXPLANATION_ACID_TEST_RATE = "Stok gibi daha az likit varlıklar çıkarılarak, şirketin daha hızlı nakde çevrilebilecek varlıklarla yükümlülüklerini karşılayabilme yeteneği değerlendirilir."

    const val LABEL_NET_DEBT_EQUITY_RATIO_VALUE = "D/E"
    const val EXPLANATION_NET_DEBT_EQUITY_RATIO_VALUE = "Debt to Equity" // Net Borç/Özsermaye Oranı
    const val EXTENDED_EXPLANATION_NET_DEBT_EQUITY_RATIO = "Şirketin finansmanını borçla mı yoksa özkaynakla mı sağladığını gösterir."

    const val LABEL_FINANCIAL_LEVERAGE_VALUE = "FL"
    const val EXPLANATION_FINANCIAL_LEVERAGE_VALUE = "Finansal Kaldıraç Oranı" // Toplam Varlıklar / Özsermaye
    const val EXTENDED_EXPLANATION_FINANCIAL_LEVERAGE_VALUE = "Şirketin varlıklarını ne ölçüde borçla finanse ettiğini gösterir."

    const val LABEL_INTEREST_COVERAGE_VALUE = "IC"
    const val EXPLANATION_INTEREST_COVERAGE_VALUE = "Faiz Karşılama Oranı" // Faaliyet Karı (EBIT) / Faiz Giderleri
    const val EXTENDED_EXPLANATION_INTEREST_COVERAGE_VALUE = "Şirketin faiz ödemelerini karıyla karşılama kapasitesini değerlendirir."
}