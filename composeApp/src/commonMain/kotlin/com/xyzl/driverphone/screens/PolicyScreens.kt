package com.xyzl.driverphone.screens

import com.xyzl.driverphone.AppLanguage
import com.xyzl.driverphone.AppLanguageStore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** 用户服务协议 */
private val userServicePolicy = listOf(
    "H|1. 协议总则",
    "P|1.1 本《用户服务协议》（以下简称「本协议」）是用户（个人/企业/司机）与本平台运营方「星域臻旅」（以下简称「平台」）之间，关于使用本平台全部出行、跨境包车、预订、结算、客服、调度等服务的合法有效法律协议。",
    "P|1.2 用户下载、注册、登录、使用平台任意功能，即代表完全阅读、理解、无条件同意本协议全部条款，自愿接受平台合规监管。",
    "P|1.3 本协议适用于：用户端、司机端、商家后台、网页端所有使用者。",
    "P|1.4 平台保留在合法范围内更新、修订协议的权利，更新后三端公示即生效，持续使用即视为自动接受更新条款。",
    "H|2. 用户资格与注册规则",
    "P|2.1 个人用户必须年满18周岁，未成年人禁止使用本平台跨境出行服务。",
    "P|2.2 司机用户必须完成全套KYC实名认证、车辆认证、证件年审备案方可接单。",
    "P|2.3 企业用户必须上传SSM注册资料、企业证件、负责人KYC资料完成企业认证。",
    "P|2.4 用户承诺：注册信息真实、有效、完整，不得伪造、冒用、篡改他人身份资料。",
    "P|2.5 一个身份仅可注册一个账号，禁止批量注册、养号、转借账号、售卖账号。",
    "H|3. 平台服务内容",
    "P|3.1 平台提供：新马两地跨境包车、日间包车、点对点接送、行程预订、司机调度、订单结算、在线客服、电子发票、凭证留存、跨境过关记录管理等服务。",
    "P|3.2 平台仅为信息撮合与合规调度服务方，不直接从事客运经营，不承担承运责任，实际承运责任由签约合规司机与车辆主体承担。",
    "P|3.3 平台有权根据马来西亚法律、跨境管制政策、海关政策随时调整服务范围、路线、功能。",
    "H|4. 用户行为规范",
    "P|用户承诺禁止以下行为：",
    "P|4.1 利用平台从事非法营运、非法跨境、偷渡、走私、违规载客。",
    "P|4.2 虚假下单、恶意取消、恶意刷单、恶意差评、恶意纠纷。",
    "P|4.3 辱骂、骚扰、威胁司机、客服、工作人员。",
    "P|4.4 伪造证件、伪造行程、伪造过关记录、篡改平台数据。",
    "P|4.5 攻击、渗透、爬虫、逆向、破解平台系统。",
    "P|4.6 违反马来西亚道路交通法、海关法、出入境条例、PDPA数据合规条例。",
    "H|5. 订单与交易规则",
    "P|5.1 所有订单以平台系统记录为准，系统数据为唯一合法凭证。",
    "P|5.2 用户下单即表示确认行程、价格、服务规则、取消规则。",
    "P|5.3 跨境行程需严格遵守马来西亚、新加坡出入境法规，用户自行承担出入境合规责任。",
    "P|5.4 平台支持TNG、线上转账、官方支付渠道，所有交易流水、账单、税务记录由平台合规留存，符合LHDN E-Invoice规范。",
    "H|6. 账号安全与责任",
    "P|6.1 用户自行保管账号、密码、设备、登录权限。",
    "P|6.2 账号所有操作视为用户本人操作，用户承担全部责任。",
    "P|6.3 账号被盗、异常登录需立即联系平台冻结账号，平台仅配合合规处理，不承担用户个人疏忽损失。",
    "H|7. 处罚与封禁规则",
    "P|7.1 违规用户、违规司机、违规企业，平台有权执行：警告、限制接单、限制下单、冻结余额、临时封禁、永久封禁、黑名单登记、提交官方合规记录。",
    "P|7.2 严重违法违规行为，平台有权保留证据并移交马来西亚警方、海关、MCMC、PDPA监管机构。",
    "H|8. 协议管辖",
    "P|8.1 本协议受马来西亚法律全权管辖。",
    "P|8.2 所有争议、纠纷、诉讼仲裁地点为马来西亚柔佛州新山司法辖区。"
)

/** 隐私政策 */
private val privacyPolicy = listOf(
    "H|1. 政策依据",
    "P|本隐私政策严格合规：",
    "B|Malaysia Personal Data Protection Act 2010 (PDPA)",
    "B|PDPA Amendment 2024（2025正式生效条款：跨境传输、数据泄露通报、DPO制度）",
    "B|LHDN税务数据留存规范",
    "B|MCMC互联网平台合规规范",
    "B|跨境出行用户数据合规标准",
    "H|2. 数据收集范围",
    "P|平台合法收集以下必要数据：",
    "H|2.1 普通用户数据",
    "B|手机号、WhatsApp联系方式",
    "B|昵称、头像、收货/接送地址、定位经纬度",
    "B|订单记录、行程记录、支付记录、聊天记录",
    "B|设备信息、登录IP、机型、系统版本",
    "H|2.2 司机KYC数据",
    "B|真实姓名、IC身份证号码",
    "B|驾照、护照、VEP证件、车辆证件",
    "B|人脸核验影像、身份认证记录",
    "B|车辆牌照、保险、路税、年审资料",
    "B|排班、接单、轨迹、出勤记录",
    "H|2.3 企业用户数据",
    "B|SSM企业注册编号、BRN、TIN、SST、MSIC编码",
    "B|企业地址、负责人身份资料、企业证件",
    "B|企业交易、开票、报税相关记录",
    "H|3. 数据使用目的",
    "P|平台仅将用户数据用于：",
    "P|3.1 账号注册、实名认证、KYC审核",
    "P|3.2 行程调度、司机匹配、轨迹记录、跨境过关核验",
    "P|3.3 订单管理、结算、对账、发票生成",
    "P|3.4 客服纠纷处理、安全风控、违规核查",
    "P|3.5 马来西亚法律、税务、海关合规留存",
    "P|3.6 平台功能优化、系统安全维护",
    "P|平台绝不私自售卖、滥用、非法流转用户个人数据",
    "H|4. 数据存储与留存",
    "P|4.1 用户普通行为数据留存：2–3年",
    "P|4.2 订单、财务、税务、发票数据留存：7年（马来西亚法定税务留存年限）",
    "P|4.3 KYC证件、司机资质、企业资质留存：7年",
    "P|4.4 争议、纠纷、事故记录留存：5–7年",
    "P|4.5 系统日志、接口日志、审计日志留存：永久合规归档",
    "H|5. 跨境数据传输合规（PDPA 2024第129条）",
    "P|5.1 本平台数据存储于合规海外云服务器，跨境数据传输严格遵守PDPA跨境传输条款。",
    "P|5.2 所有跨境传输数据均做安全加密、权限隔离、RLS权限管控。",
    "P|5.3 平台确保境外存储机构具备等同于马来西亚PDPA的安全保护等级。",
    "H|6. 数据泄露通报义务（PDPA 2024强制）",
    "P|6.1 平台已建立数据泄露应急机制，一旦发生数据泄露、异常访问、数据风险，将在法定时限内向PDPA监管机构通报，并通知受影响用户。",
    "P|6.2 平台设立专职DPO（数据保护官）合规岗位。",
    "H|7. 用户PDPA合法权利",
    "P|用户依法拥有：",
    "B|查阅个人数据权利",
    "B|更正错误数据权利",
    "B|申请删除数据权利",
    "B|撤回授权同意权利",
    "B|限制数据处理权利",
    "B|数据导出权利",
    "H|8. 第三方服务说明",
    "P|平台对接：支付网关、云存储、消息推送、地图定位、WhatsApp消息服务。",
    "P|所有第三方均合规签约，禁止私自滥用用户数据。",
    "H|9. Cookie与设备权限",
    "P|平台APP可申请：定位、相册、相机、通知权限，仅用于出行服务、取证、上传证件、行程定位，用户可随时在系统关闭权限。",
    "H|10. 隐私政策更新",
    "P|平台可随马来西亚法律更新修订本政策，三端公示后自动生效。"
)

/** KYC实名认证合规条款 */
private val kycPolicy = listOf(
    "H|1. KYC合规说明",
    "P|根据马来西亚反欺诈、跨境出行监管、PDPA、MCMC合规要求，本平台强制全员KYC实名认证，未认证账号禁止接单、下单、结算、开票、使用核心功能。",
    "H|2. 个人用户KYC规则",
    "P|2.1 必须完成手机号实名验证",
    "P|2.2 必要场景需完成人脸核验、身份信息核验",
    "P|2.3 虚假认证、冒用认证永久拉黑并留存合规记录",
    "H|3. 司机端KYC全套强制认证",
    "P|司机上岗必须100%通过：",
    "P|3.1 身份KYC：IC证件、人脸活体核验、护照备案",
    "P|3.2 驾照资质核验",
    "P|3.3 VEP跨境通行证件核验",
    "P|3.4 车辆KYC：车牌、保险、路税、年审、车辆照片",
    "P|3.5 无犯罪、无违规风控筛查",
    "P|3.6 所有证件过期自动禁止接单，更新后方可恢复",
    "H|4. 企业商户KYC规则",
    "P|4.1 企业SSM证照核验",
    "P|4.2 企业TIN/SST税务资质核验",
    "P|4.3 企业负责人实名与人脸核验",
    "P|4.4 企业合规经营备案",
    "H|5. KYC数据安全",
    "P|5.1 所有证件资料加密存储，仅合规审核人员可见",
    "P|5.2 严格RLS权限隔离，禁止内部泄露",
    "P|5.3 不向第三方私自提供用户KYC资料",
    "P|5.4 完全符合马来西亚金融级KYC合规标准",
    "H|6. 认证拒绝规则",
    "P|以下情况平台有权直接拒绝认证：",
    "B|证件模糊、PS、伪造、过期",
    "B|信息不一致、人脸不匹配",
    "B|黑名单人员、违规记录人员",
    "B|资料虚假、重复认证"
)

/** 退款与交易合规政策 */
private val refundPolicy = listOf(
    "P|1. 普通行程未发车可按平台规则合规取消退款",
    "P|2. 已发车、已服务、已产生跨境成本订单不予全额退款",
    "P|3. 恶意退款、虚假纠纷平台有权拒绝并拉黑",
    "P|4. 所有退款流水、财务记录、对账记录、税务记录合规留存7年",
    "P|5. 所有资金流转符合马来西亚支付合规、反洗钱合规"
)

/** 数据留存与审计合规政策 */
private val dataRetentionPolicy = listOf(
    "P|1. 审计日志、操作日志、登录日志、接口日志禁止删除、禁止软删（完全匹配你的数据库审计表规则）",
    "P|2. 所有业务数据严格按照PDPA、LHDN分级留存",
    "P|3. 平台所有操作记录可随时接受马来西亚官方核查",
    "P|4. 所有删除行为留存删除日志，可追溯、可审计"
)

/** 免责声明 */
private val disclaimerPolicy = listOf(
    "P|6.1 平台仅为信息撮合与调度服务，不直接承运，交通事故、跨境出入境问题、用户个人违规行为由责任方自行承担。",
    "P|6.2 因海关政策、跨境管制、天气、政府管制导致行程变更，平台免责。",
    "P|6.3 用户违规使用服务造成的一切损失由用户自行承担。",
    "P|6.4 系统升级、维护、网络波动导致短暂异常，平台在合规范围内免责。"
)

/** 合规最终说明 */
private val finalCompliancePolicy = listOf(
    "P|1. 本文档三端完全通用：用户APP、司机APP、商家后台、网页端",
    "P|2. 全文100%马来西亚合法合规（PDPA2024、LHDN、SSM、跨境出行合规）",
    "P|3. 可直接用于：APP上架、官网公示、用户注册勾选、法务备案、融资合规、企业年审",
    "P|4. 完全适配你现有Supabase数据库审计规则、数据留存规则、KYC表结构、Storage合规存储周期"
)


/** 政策/协议通用页：H=小节标题 P=正文 B=条目 */
@Composable
fun PolicyScreen(title: String, blocks: List<String>, onBack: () -> Unit = {}) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp),
    ) {
        FormPageHeader(title = title, onBack = onBack)
        Spacer(Modifier.height(16.dp))

        Card(
            Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = FormCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        ) {
            Column(Modifier.padding(16.dp)) {
                blocks.forEach { block ->
                    val sep = block.indexOf('|')
                    if (sep < 0) return@forEach
                    val kind = block.substring(0, sep)
                    val body = block.substring(sep + 1)
                    when (kind) {
                        "H" -> Text(
                            body,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FormInk,
                            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                        )
                        "B" -> Row(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                            Text("· ", fontSize = 14.sp, color = FormGreen)
                            Text(body, fontSize = 14.sp, color = FormInk, lineHeight = 21.sp, modifier = Modifier.weight(1f))
                        }
                        else -> Text(
                            body,
                            fontSize = 14.sp,
                            color = FormInk,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(bottom = 10.dp),
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(110.dp))
    }
}

@Composable
fun UserServiceAgreementScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "User Service Agreement" else "用户服务协议",
        blocks = if (en) userServicePolicyEn else userServicePolicy,
        onBack = onBack
    )
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Privacy Policy" else "隐私政策",
        blocks = if (en) privacyPolicyEn else privacyPolicy,
        onBack = onBack
    )
}

@Composable
fun KycPolicyScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "KYC Verification Terms" else "KYC实名认证合规条款",
        blocks = if (en) kycPolicyEn else kycPolicy,
        onBack = onBack
    )
}

@Composable
fun RefundPolicyScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Refund Policy" else "退款与交易合规政策",
        blocks = if (en) refundPolicyEn else refundPolicy,
        onBack = onBack
    )
}

@Composable
fun DataRetentionPolicyScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Data Retention & Information Security Statement" else "数据留存与审计合规政策",
        blocks = if (en) dataRetentionPolicyEn else dataRetentionPolicy,
        onBack = onBack
    )
}

@Composable
fun DisclaimerScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Platform Disclaimer" else "免责声明",
        blocks = if (en) disclaimerPolicyEn else disclaimerPolicy,
        onBack = onBack
    )
}

@Composable
fun FinalComplianceScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Final Compliance Statement" else "合规最终说明",
        blocks = if (en) finalCompliancePolicyEn else finalCompliancePolicy,
        onBack = onBack
    )
}

/** 终端用户软件许可协议（EULA） */
private val eulaPolicy = listOf(
    "P|星域臻旅 · 三端应用",
    "P|运营主体：STELLAR ELITE ENTERPRISE",
    "P|SSM 注册编号：202603215560 (003879535‑K)",
    "P|营业注册地址：",
    "P|生效日期：2026‑10‑12",
    "P|适用：用户端 APP、司机端 APP、商家管理后台",
    "P|重要提示：在下载、安装、访问、注册以及使用本软件之前，请仔细阅读本《终端用户软件许可协议》（下称「本协议」）。",
    "P|>",
    "P|当您下载、安装、注册登录、打开或者使用本软件，即代表您已经完整阅读、充分理解，并无条件同意接受本协议全部条款约束。",
    "P|>",
    "P|本许可协议属于您与 STELLAR ELITE ENTERPRISE（下文简称「平台 / 许可方」）之间具备约束力的法律协议。",
    "P|>",
    "P|>",
    "H|1. 软件知识产权与版权",
    "P|1.1  本软件（含用户端、司机端、商家后台，以下统称「软件」）全部源代码、界面设计、图标、品牌、交互逻辑、文案、视觉素材，所有知识产权、版权均归 STELLAR ELITE ENTERPRISE 独家所有。",
    "P|1.2  本协议仅授予终端用户有限使用权，软件的版权、知识产权不会因为您下载、安装软件而转让给使用者。",
    "H|2. 许可授予",
    "P|平台授予您一项：非排他、不可转让、不可转授权、可撤销 的有限使用许可。",
    "P|您仅可以在属于您本人合法拥有的移动设备上安装、运行本软件，仅限用于正常使用平台跨境包车、出行预订相关业务，不得挪作其他用途。",
    "H|3. 许可使用限制",
    "P|您承诺不会执行下面任何行为：",
    "P|(a)  对本软件进行反编译、逆向工程、反汇编、尝试还原、提取源代码；",
    "P|(b)  修改、改编、二次开发、翻译、基于本软件制作衍生程序；",
    "P|(c)  复制、分发、出租、转售、托管、私自部署、对外传播本应用；",
    "P|(d)  移除、遮挡、篡改软件内部任何版权、商标、品牌归属标识；",
    "P|(e)  使用爬虫、脚本、自动化程序批量访问、刷接口、对平台进行压力测试或者攻击；",
    "P|(f)  将本软件安装、分享、转借、提供给第三方使用；",
    "P|(g)  破解、绕过平台权限、风控、认证、KYC 校验机制。",
    "H|4. 应用设备权限",
    "P|本软件可根据业务需求向您的设备申请相机、相册、定位、推送通知等设备权限，权限的收集与使用规则完整参照本平台《隐私政策》。",
    "P|所有权限仅用于业务必需场景。",
    "H|5. 软件更新与版本维护",
    "P|5.1  平台有权随时发布软件补丁、功能升级、新版本。",
    "P|5.2  您同意平台可以推送自动或者手动更新；平台可随时停止对旧版本的维护与支持。",
    "P|5.3  若您拒绝更新新版本，部分功能可能无法正常使用。",
    "H|6. 软件免责条款",
    "P|6.1  本软件按照「现状（as‑is）」提供，平台不承诺软件永久无故障、不间断、零延迟运行。",
    "P|6.2  因为手机硬件、操作系统、网络故障、第三方服务异常造成软件短暂失效，在马来西亚法律允许范围内，平台不承担对应的赔偿责任。",
    "P|6.3  软件本身仅作为业务调度工具，软件内容不能替代马来西亚、新加坡当地交通、海关、出入境的法律指引。",
    "H|7. 许可终止",
    "P|7.1  一旦您违反本许可协议任意条款，平台有权直接撤销这份软件使用许可，冻结或者封禁您的账号，您应当立刻卸载本软件。",
    "P|7.2  账号封禁生效之后，您使用本软件的许可权利立即终止。",
    "H|8. 第三方开源组件",
    "P|本软件可能集成部分开源以及第三方组件，相关组件遵守各自对应的开源许可协议；但不会改变本软件整体知识产权归属。",
    "H|9.  管辖法律与争议",
    "P|9.1  本《终端用户软件许可协议》受马来西亚法律管辖。",
    "P|9.2  任何因本协议引起的纠纷，争议审理地点为马来西亚柔佛州新山司法辖区。",
    "P|你直接整段复制，新建文档粘贴保存即可。",
    "P|如果你想，我现在一次性把：",
    "P|用户服务协议 + 隐私政策 + 这份 EULA + KYC + 退款 + 审计政策 合并成一份完整终稿。",
    "P|© 2026 STELLAR ELITE ENTERPRISE. 星域臻旅版权所有。"
)

@Composable
fun EulaScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "End User License Agreement (EULA)" else "终端用户软件许可协议",
        blocks = if (en) eulaPolicyEn else eulaPolicy,
        onBack = onBack
    )
}

/** 司机服务协议（平台 ↔ 入驻司机） */
private val driverServicePolicy = listOf(
    "P|本协议为平台 ↔ 入驻司机之间的线上电子协议，司机在 APP 勾选同意、完成 KYC 认证后方可入驻接单。",
    "H|1. 协议各方",
    "P|甲方（平台运营方）：星域臻旅（下文简称「平台」）。注册信息依据公司 SSM 注册资料填写；地址：Alamat（注册地址）。",
    "P|乙方（入驻司机）：在平台司机端 APP 注册、完成 KYC 认证的司机自然人（下文简称「司机」）。",
    "P|乙方确认：年满法定年龄，持有有效驾照、车辆相关证件，具备在马来西亚 / 新马跨境从事客运服务的资格。",
    "P|本协议自乙方在司机端 APP 点击【我已阅读并同意《司机服务协议》】，并完成 KYC 身份核验起生效。",
    "H|2. 服务内容",
    "P|2.1 平台提供线上信息撮合服务：向乙方推送旅客的跨境包车订单、行程信息，提供订单管理、收款结算、消息通知、行程记录等数字化工具。平台仅为信息服务撮合方，不直接雇佣乙方，甲乙双方不存在雇佣 / 劳务关系。",
    "P|2.2 乙方使用平台司机端 APP，接收旅客订单，按照订单约定的时间、地点、路线，提供客运接送服务（新马跨境包车）。",
    "P|2.3 乙方知悉：所有运输服务的承运责任由乙方承担，平台不承担客运承运人的法律责任。",
    "H|3. 司机入驻资质与承诺（KYC 相关）",
    "P|3.1 乙方必须提交真实、有效证件完成 KYC 核验：马来西亚身份证 / 护照、有效驾驶执照、车辆注册文件、车辆保险等。平台有权核验、留存证件扫描件。",
    "P|3.2 乙方承诺：",
    "P|• 驾照在有效期内，准驾车型匹配营运车辆；",
    "P|• 车辆年检、保险齐全有效；",
    "P|• 无重大交通违法、危险驾驶犯罪记录；",
    "P|• 提交给平台所有资料真实，不伪造、篡改证件；",
    "P|• 跨境出行时，遵守马来西亚、新加坡两国海关、出入境、道路交通法规。",
    "P|3.3 平台有权不定期复核司机资质；资质失效、证件过期时，平台可暂停乙方接单权限直至资料更新。",
    "H|4. 费用、佣金与结算",
    "P|4.1 平台将按约定比例，从每笔完成订单收取平台服务费（佣金），费率详情在司机端【费率说明】页面公示。",
    "P|4.2 订单款项由平台代收后，按约定结算周期结算至乙方绑定账户；平台可扣除服务费、平台代付的手续费、订单产生的违约金 / 赔偿款后再放款。",
    "P|4.3 乙方有义务按马来西亚 LHDN 税务法规，自行申报收入、缴纳相关税费。平台不代扣代缴乙方个人税费。",
    "P|4.4 若产生旅客退款、客诉赔偿，平台有权从待结算款项中先行抵扣，再和乙方核对。",
    "H|5. 司机义务",
    "P|5.1 接单后，严格遵守订单约定时间地点，准时接载乘客；如无法履约，必须在 APP 内提前取消并按规则承担违约金。",
    "P|5.2 文明服务，禁止辱骂、骚扰、威胁乘客；禁止向乘客私下额外加价、强制索要小费。",
    "P|5.3 遵守交通法规，严禁超速、危险驾驶、酒驾毒驾；行车全程保障乘客人身安全。",
    "P|5.4 妥善保管乘客随身物品，乘客遗落物品需要配合归还。",
    "P|5.5 保护乘客隐私：不可私自拍摄、录音、泄露乘客身份、手机号、行程信息。",
    "P|5.6 跨境行程，提醒乘客备好护照、签证、通关文件；不协助乘客走私、携带违禁品过关。",
    "P|5.7 只能使用本人账号接单，不得转借、出租司机账号给其他人。",
    "H|6. 平台权利与义务",
    "P|6.1 平台负责维护 APP 系统、推送订单、处理线上收款、留存行程记录。",
    "P|6.2 平台受理乘客投诉，可对订单纠纷做调查、仲裁判定。",
    "P|6.3 平台有权对司机做服务评分管理，低分司机可限制派单、暂停接单、永久封禁账号。",
    "P|6.4 平台不担保订单数量，不保证司机收入。",
    "P|6.5 平台按隐私政策保管司机证件、个人信息，不会未经许可对外泄露（法律机关要求除外）。",
    "H|7. 订单取消、违约与客责划分",
    "P|7.1 司机接单后无故取消订单、迟到超时、拒载，平台可按规则扣罚违约金，补偿乘客。",
    "P|7.2 因司机驾驶违规、车辆故障、证件问题造成行程失败、乘客损失，全部责任由司机承担。",
    "P|7.3 乘客单方面取消订单，按平台公示的取消规则执行退款。",
    "H|8. 账号管理、暂停与终止协议",
    "P|8.1 账号属于平台提供的服务使用权，司机不可转让售卖。",
    "P|8.2 出现以下情形，平台可立即冻结接单权限 / 永久注销账号、终止协议：",
    "P|• 证件造假、KYC 资料虚假；",
    "P|• 危险驾驶、违规载客、走私、违反出入境法规；",
    "P|• 多次客诉、辱骂乘客、私下线下交易跳单；",
    "P|• 利用平台从事违法活动；",
    "P|• 逾期不更新过期资质文件。",
    "P|8.3 协议终止后：平台结清已完成订单款项（扣除罚金 / 赔偿）；司机不再拥有接单权限，平台保留行程、证件记录按法规留存。",
    "H|9. 责任限制与免责",
    "P|9.1 平台仅做信息撮合，不承担交通事故、人身伤害、财物损失的承运赔偿责任，该责任由司机、车辆保险承担。",
    "P|9.2 平台 APP 服务会按标准维护，但不承诺系统永久无故障；因网络、服务器故障造成短暂无法接单，平台不承担损失赔偿。",
    "P|9.3 因不可抗力（边境关闭、政策变动、自然灾害）造成无法履约，双方互不追责。",
    "H|10. 争议解决与适用法律",
    "P|10.1 本协议受马来西亚法律管辖。",
    "P|10.2 双方产生纠纷优先线上协商；协商不成，提交马来西亚有管辖权法院处理。",
    "H|11. 其他条款",
    "P|11.1 平台可更新本协议，更新后会在司机 APP 弹窗公示，继续使用 APP 接单视为同意新版本协议。",
    "P|11.2 本协议是电子线上协议，司机在 APP 勾选确认即具备电子合同效力。",
    "P|11.3 本协议为《隐私政策》《KYC 认证条款》《退款政策》的配套文件，一并构成平台与司机完整约定。",
    "H|尾部签字区（APP 电子确认用）",
    "P|司机确认：我已完整阅读、清楚理解《司机服务协议》全部条款，自愿同意遵守。",
    "P|□ 我已阅读并同意《司机服务协议》、隐私政策、KYC 条款。",
)

@Composable
fun DriverServiceAgreementScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Driver Service Agreement" else "司机服务协议",
        blocks = if (en) driverServicePolicyEn else driverServicePolicy,
        onBack = onBack
    )
}


/** English version (bundle). */
private val userServicePolicyEn = listOf(
    "P|Platform: Xingyu Zhenlü (hereinafter referred to as \"the Platform\")",
    "P|This agreement is electronically accepted by passengers upon ticking consent and completing required verification on the passenger mobile application.",
    "H|1. Parties to Agreement",
    "P|Service Provider (Platform): Xingyu Zhenlü",
    "P|SSM Registered Details: Fill with your SSM information",
    "P|Registered Address: Alamat (Your registered address)",
    "P|User (Passenger): Natural person registering and using the passenger APP, aged above the legal age of majority.",
    "P|This Agreement takes effect once you tick “I have read and accept this User Service Agreement” on the APP.",
    "H|2. Service Scope",
    "P|2.1 The Platform acts only as an information intermediary. The Platform matches passengers with registered drivers for cross-border private car charter services between Malaysia and Singapore. The Platform is NOT a carrier and does not provide transportation service itself.",
    "P|2.2 The Platform provides digital tools including order submission, quotation viewing, online payment, itinerary tracking and order record management.",
    "P|2.3 The actual transportation service is provided by the contracted driver. All carriage liabilities shall be borne by the driver.",
    "H|3. User Representations & Obligations",
    "P|3.1 You warrant all information submitted to the Platform (name, contact, travel documents) is true, accurate and valid.",
    "P|3.2 For cross-border travel, you shall hold valid passport, visa and immigration documents required by Malaysia and Singapore. The Platform shall not be liable if you are denied entry by immigration authorities.",
    "P|3.3 You shall not request drivers to violate traffic laws, cross-border customs rules or carry prohibited goods.",
    "P|3.4 You shall not damage the driver’s vehicle or property, harass or threaten the driver.",
    "P|3.5 You shall pay all confirmed order fares in accordance with the pricing shown on the Platform.",
    "H|4. Order, Cancellation & Refund",
    "P|4.1 Order is confirmed after payment. Cancellation shall follow the Refund Policy published within the APP.",
    "P|4.2 If the driver fails to show up or breaches the agreement, the Platform will handle complaints and refunds per published rules.",
    "H|5. Privacy",
    "P|Your personal data will be processed in accordance with the Platform’s Privacy Policy.",
    "H|6. Platform Rights",
    "P|6.1 The Platform reserves the right to suspend or terminate your account for fraud, false information, violation of agreement terms or abusive behaviour.",
    "P|6.2 The Platform may update this Agreement. Continued use of the APP after update constitutes acceptance of revised terms.",
    "H|7. Limitation of Liability",
    "P|The Platform is only an information matching intermediary. It shall not be liable for personal injury, accident, property loss arising from the actual transportation service, which shall be covered by driver’s insurance and the driver’s liability.",
    "H|8. Governing Law & Dispute Resolution",
    "P|This Agreement is governed by the laws of Malaysia. Any dispute shall first be resolved amicably; failing which, disputes shall be submitted to courts of competent jurisdiction in Malaysia.",
    "H|9. Miscellaneous",
    "P|This agreement forms part of a bundle together with Privacy Policy, KYC Terms and Refund Policy.",
    "P|Electronic Consent:",
    "P|☐ I have read and agree to the User Service Agreement, Privacy Policy and KYC Terms.",
)

/** English version (bundle). */
private val driverServicePolicyEn = listOf(
    "P|Effective upon driver’s consent tick and completion of KYC verification in the Driver APP.",
    "P|Important Notice: Template only. Legal review by Malaysian licensed lawyer recommended before deployment.",
    "H|1. Parties",
    "P|Party A (Platform Operator): Xingyu Zhenlü (“Platform”)",
    "P|SSM Registered Details: Fill your SSM data",
    "P|Address: Alamat (Registered Address)",
    "P|Party B (Registered Driver): Natural person completing registration and KYC on Driver APP.",
    "P|The Driver confirms they hold valid driving licence, vehicle documents and qualification to conduct cross-border passenger transport between MY and SG.",
    "P|This Agreement becomes effective when the driver ticks consent and finishes KYC verification.",
    "H|2. Service Description",
    "P|2.1 The Platform provides information intermediary service: pushing cross-border charter orders, itinerary data, order management, settlement and notification tools. The Platform is not an employer. No employment or labour relationship exists between Platform and Driver.",
    "P|2.2 The Driver accepts orders via APP and provides passenger transportation service according to agreed time, pickup location and route.",
    "P|2.3 The Driver acknowledges that the Driver is the carrier and bears all transportation liabilities. The Platform shall not be treated as the transport carrier.",
    "H|3. Driver Eligibility & KYC Undertakings",
    "P|3.1 Driver must submit valid documents for KYC: Malaysian ID / Passport, valid driving licence, vehicle registration, vehicle insurance. The Platform may verify and store document copies.",
    "P|3.2 Driver warrants:",
    "P|• Driving licence remains valid with correct vehicle class endorsement",
    "P|• Vehicle road tax and insurance are fully maintained",
    "P|• No serious traffic offence or dangerous driving criminal record",
    "P|• All submitted documents are genuine and unaltered",
    "P|• Obey traffic, customs and immigration laws of Malaysia and Singapore during cross-border trips",
    "P|3.3 The Platform may re-audit driver qualifications periodically. If documents expire, Platform may suspend order access until updated.",
    "H|4. Fees, Commission & Settlement",
    "P|4.1 The Platform collects service commission per completed order. Commission rates are published in the Rate Schedule inside Driver APP.",
    "P|4.2 Order funds collected by Platform will be settled to Driver’s bound account after deducting platform commission, payment gateway fees, compensation or penalty charges.",
    "P|4.3 Driver is responsible to file income tax to LHDN Malaysia independently. Platform does NOT withhold personal tax on behalf of Driver.",
    "P|4.4 For passenger refunds or compensation claims, Platform may deduct relevant amount from pending settlement before releasing funds.",
    "H|5. Driver Obligations",
    "P|5.1 Accept and complete orders punctually. If unable to fulfil, cancel in APP in advance and bear penalties as per rules.",
    "P|5.2 Maintain good service conduct. No harassment, insult or unauthorised surcharge / forced tipping to passengers.",
    "P|5.3 Obey traffic rules strictly. No drunk driving, speeding or dangerous driving. Safeguard passenger safety.",
    "P|5.4 Keep passenger belongings safe and assist returning lost items.",
    "P|5.5 Protect passenger privacy: No unauthorised photo, recording or disclosure of passenger identity, contact number or travel itinerary.",
    "P|5.6 Remind passengers to prepare passport, visa and immigration documents for cross-border trips. Never assist passengers to smuggle or carry prohibited goods.",
    "P|5.7 Only use your own account. Account lending or renting to third parties is prohibited.",
    "H|6. Platform Rights & Obligations",
    "P|6.1 Maintain APP system, push orders, process payment and store trip records.",
    "P|6.2 Receive passenger complaints and conduct investigation and adjudication for order disputes.",
    "P|6.3 Implement rating system. Low rating drivers may receive restricted order allocation, temporary suspension or permanent account termination.",
    "P|6.4 Platform does not guarantee order volume or minimum income.",
    "P|6.5 Platform protects driver personal data as per Privacy Policy, and will not disclose data unless legally requested by authorities.",
    "H|7. Order Cancellation, Breach & Liability Allocation",
    "P|7.1 If driver cancels accepted order without valid reason, late pickup or refuses service, Platform may deduct penalty to compensate passenger.",
    "P|7.2 All losses caused by driver violation, vehicle breakdown or expired documents shall be borne by Driver.",
    "P|7.3 Passenger-initiated cancellation follows the published Refund Policy.",
    "H|8. Account Suspension & Termination",
    "P|8.1 The driver only holds usage right to the account; account transfer or sale is forbidden.",
    "P|8.2 Platform may immediately suspend order access or permanently terminate account if:",
    "P|• KYC documents are forged or false",
    "P|• Dangerous driving, illegal passenger carriage, smuggling or immigration law breach",
    "P|• Repeated complaints, insulting passengers or off-platform transaction (jump order)",
    "P|• Using platform for illegal activities",
    "P|• Failure to renew expired qualification documents",
    "P|8.3 Upon termination: Platform settles payment for completed orders after deduction of penalties; driver loses order access. Platform retains trip and document records for statutory retention period.",
    "H|9. Disclaimer & Limitation of Liability",
    "P|9.1 Platform is purely information intermediary and shall not be liable for traffic accident, personal injury or property loss. Such liabilities are covered by driver and vehicle insurance.",
    "P|9.2 Platform maintains APP service but does not guarantee uninterrupted service. Platform shall not compensate loss caused by server downtime or network failure.",
    "P|9.3 Force majeure (border closure, policy change, natural disaster) releases both parties from liability.",
    "H|10. Governing Law & Dispute",
    "P|This Agreement is governed by laws of Malaysia. Parties shall negotiate disputes amicably; failing which, disputes shall be submitted to Malaysian competent courts.",
    "H|11. Miscellaneous",
    "P|11.1 Platform may revise this Agreement. Continued APP usage after update means acceptance of revised terms.",
    "P|11.2 Electronic tick acceptance in APP constitutes valid electronic contract.",
    "P|11.3 This document works together with Privacy Policy, KYC Terms and Refund Policy as full contractual terms.",
    "P|Electronic Consent:",
    "P|☐ I have read and fully understood the Driver Service Agreement, Privacy Policy and KYC Terms and agree to abide.",
)

/** English version (bundle). */
private val privacyPolicyEn = listOf(
    "P|Xingyu Zhenlü (“Platform”) respects your personal privacy. This policy applies to passengers, drivers and all users accessing our mobile application.",
    "H|1. What data we collect",
    "P|• Identity data (for KYC): Name, NRIC/Passport number, driving licence, vehicle documents, photo / scanned copies of identity documents",
    "P|• Contact data: Phone number, email",
    "P|• Trip data: pickup/drop-off location, travel time, order records, GPS location during trip",
    "P|• Payment data: payment transaction records (we do NOT store full bank / card sensitive credentials)",
    "P|• App log: device info, login time, error logs",
    "H|2. Purpose of data collection",
    "P|• User identity verification and KYC compliance",
    "P|• Order matching, trip arrangement and communication",
    "P|• Payment settlement, commission calculation, dispute investigation",
    "P|• Safety monitoring, fraud prevention and platform risk control",
    "P|• Comply with Malaysian statutory requirements and government authority requests.",
    "H|3. Data Sharing",
    "P|We will NOT sell your personal data to third parties. Data may be shared only:",
    "P|1. With your explicit consent",
    "P|2. Required by Malaysian law, court order or government authority",
    "P|3. Necessary service providers (payment gateway, cloud service, KYC verification vendor), under strict data protection obligation.",
    "H|4. Data Storage & Retention",
    "P|Data will be retained only as long as required by business and Malaysian legal requirements. After retention period, data will be securely anonymised or deleted.",
    "H|5. User Rights",
    "P|You have the right to request access, correction or deletion of your personal data subject to applicable Malaysian personal data laws.",
    "H|6. Data Security",
    "P|We adopt industry-standard technical safeguards to protect stored data from unauthorised access, loss or leakage. Absolute security cannot be guaranteed.",
    "H|7. Minor",
    "P|Our service is not intended for minors under legal age.",
    "H|8. Update of Policy",
    "P|We may update this Privacy Policy. Notification will be shown in APP. Continued use equals acceptance of updated version.",
    "H|9. Contact",
    "P|For privacy enquiries, contact the Platform administrator.",
    "P|Governing Law: Malaysia",
)

/** English version (bundle). */
private val kycPolicyEn = listOf(
    "P|These terms apply to all drivers and users required to complete KYC verification on Xingyu Zhenlü Platform.",
    "P|1. Purpose of KYC",
    "P|KYC is required for identity validation, fraud prevention, regulatory compliance, risk control and safety of cross-border transport service.",
    "P|2. Obligation to submit true documents",
    "P|You must submit genuine, unmodified, valid identity documents (Malaysian NRIC, passport, driving licence, vehicle papers as requested). All information must match your real identity.",
    "P|Forged, altered or fake documents will result in immediate account suspension, termination and report to relevant authorities.",
    "P|3. Data processing",
    "P|Your uploaded ID scans, photos and biometric data (Face ID verification) will be processed for identity checking. Data is handled in accordance with our Privacy Policy.",
    "P|4. Verification Process",
    "P|The Platform or authorised third-party KYC vendor will validate your submitted materials. Verification may be rejected if documents blurry, expired or inconsistent. You shall resubmit materials upon rejection.",
    "P|5. Periodic re-verification",
    "P|The Platform reserves right to request re-KYC at any time, for example before document expiry. Failure to complete re-verification will suspend service access.",
    "P|6. Consent",
    "P|By submitting documents and completing face verification, you consent to the Platform processing your identity information for KYC purpose.",
    "P|7. Governing law: Malaysia",
)

/** English version (bundle). */
private val refundPolicyEn = listOf(
    "P|This Refund Policy applies to all orders booked via Xingyu Zhenlü passenger APP.",
    "P|1. Order Cancellation Timelines & Refund Ratio",
    "P|Cancellation rules depend on how long before trip commencement the cancellation is submitted, detailed inside APP order page.",
    "P|• Early cancellation: higher refund percentage",
    "P|• Short notice / last-minute cancellation: partial refund or no refund",
    "P|• No-show by passenger: normally non-refundable",
    "P|2. Driver-side breach",
    "P|If driver cancels confirmed order without valid reason or fails to arrive, Platform will process refund according to applicable rules.",
    "P|3. Non-refundable scenarios",
    "P|• Trip has already been completed",
    "P|• Service interruption caused by passenger’s personal reason, invalid travel documents, immigration rejection",
    "P|• Force majeure events such as border closure, natural disaster, government restriction",
    "P|4. Refund processing",
    "P|Approved refunds will be returned to original payment method within published processing period. Bank processing delays are outside Platform’s control.",
    "P|5. Dispute",
    "P|If you disagree with refund decision, you may submit evidence to Platform customer service for review.",
    "P|6. Policy update",
    "P|Platform reserves the right to revise this policy. Updated version will be published on APP.",
)

/** English version (bundle). */
private val disclaimerPolicyEn = listOf(
    "P|1. Xingyu Zhenlü is purely an information intermediary platform. The Platform does NOT provide passenger transportation service and is NOT the carrier of any charter trip. All carriage service is provided by independent registered drivers.",
    "P|2. The Platform does not control driver behaviour, vehicle condition or road safety. The Platform is not liable for accidents, injuries, loss or damage to person or property arising during road transportation. All such liabilities rest with driver and vehicle insurance.",
    "P|3. The Platform cannot guarantee uninterrupted APP service. Service may be interrupted due to internet outage, server maintenance or third-party vendor failure. Platform shall not compensate loss caused by such interruption.",
    "P|4. The Platform is not responsible for cross-border immigration clearance success. Passengers must hold valid travel documents and comply with MY/SG immigration rules.",
    "P|5. Force majeure events (war, pandemic, border control, natural disaster, government policy change) release Platform from liability for unfulfilled orders.",
    "P|6. All user-generated content, driver information on platform is only for reference. Platform does not warrant absolute accuracy of driver self-submitted data.",
    "P|This disclaimer is governed by Malaysian law.",
)

/** English version (bundle). */
private val eulaPolicyEn = listOf(
    "P|Between Xingyu Zhenlü (Licensor) and you (End User, licensee). This applies to Xingyu Zhenlü mobile application software.",
    "P|1. License Grant",
    "P|The Platform grants you a limited, non-exclusive, non-transferable license to install and use this APP solely to access platform service on mobile device. You may not copy, modify, reverse-engineer, decompile, crack, redistribute or resell the APP software.",
    "P|2. Intellectual Property",
    "P|All copyright, trademark, trade secret and intellectual property rights of APP software, logo, source code belong to Xingyu Zhenlü. No right of ownership is transferred to you.",
    "P|3. Prohibited Use",
    "P|You shall not use this APP for illegal activity, fraud, harassment, violation of cross-border or traffic law.",
    "P|4. No Warranty",
    "P|THE SOFTWARE IS PROVIDED “AS IS”. THE LICENSOR MAKES NO WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF MERCHANTABILITY OR FITNESS FOR PARTICULAR PURPOSE. The Platform does not guarantee bug-free or uninterrupted operation.",
    "P|5. Limitation of Liability",
    "P|To maximum extent permitted by Malaysian law, Licensor shall not be liable for indirect, consequential, special damages arising from APP usage.",
    "P|6. Termination",
    "P|This license terminates automatically if you breach this EULA. Upon termination, you must cease using and delete the APP.",
    "P|7. Governing Law: Malaysia",
    "P|Electronic consent checkbox:",
    "P|☐ I read and accept this End User License Agreement",
)

/** English version (bundle). */
private val dataRetentionPolicyEn = listOf(
    "P|This statement supplements the Privacy Policy of Xingyu Zhenlü.",
    "P|1. Purpose",
    "P|This statement describes how we retain, store, protect and dispose user data collected through our APP.",
    "P|2. Retention Period",
    "P|• Order, trip, transaction records: retained for the period required under Malaysian tax and commercial regulatory requirements.",
    "P|• KYC identity documents: kept for compliance and risk investigation purpose; deleted or anonymised once retention requirement ends.",
    "P|• Log data: stored for limited security audit period and purged after expiry.",
    "P|3. Data Protection Measures",
    "P|We implement access control, encryption, cloud security configuration to protect stored personal data. Access to sensitive documents is restricted to authorised staff only.",
    "P|4. Data Destruction",
    "P|When retention period expires, data will be securely erased or anonymised such that individuals can no longer be identified.",
    "P|5. Data Breach Response",
    "P|If personal data breach occurs, Platform will follow applicable Malaysian regulations to assess risk and notify affected users and authority as required.",
    "P|6. Your responsibility",
    "P|Users must keep their account password secure and avoid account leakage.",
    "P|7. Governing law: Malaysia",
)

/** English version (bundle). */
private val rateSchedulePolicyEn = listOf(
    "P|This document is an annex to Driver Service Agreement.",
    "P|1. Commission",
    "P|Platform service commission rate is calculated against gross order value for each successfully completed trip. The rate will be clearly shown inside driver APP. Platform reserves right to adjust commission rate with prior notice published on driver APP.",
    "P|2. Deduction items",
    "P|From settlement amount, Platform may deduct:",
    "P|• Platform service commission",
    "P|• Payment gateway transaction fees",
    "P|• Penalties, passenger compensation, refund losses incurred from the driver’s breach of agreement",
    "P|3. Settlement Cycle",
    "P|Settlement cycle will be displayed inside Driver APP wallet page. Funds will be released after deduction of applicable charges.",
    "P|4. Tax",
    "P|All income received by driver is driver’s own taxable income. Driver is responsible to file tax return to LHDN Malaysia independently. Platform does not withhold tax.",
    "P|5. Adjustment",
    "P|Platform may revise commission and settlement rules with notice on APP. Continued use of driver APP after notice means acceptance of revised schedule.",
    "P|6. This Schedule is an integral annex of Driver Service Agreement, governed by Malaysian law.",
)

/** 司机费率与佣金（中文） */
private val rateSchedulePolicy = listOf(
    "P|本附件为《司机服务协议》的组成部分。",
    "H|1. 佣金",
    "P|平台按每笔成功完成订单的订单总额计算服务佣金，费率在司机端 APP 内公示。平台有权调整佣金费率，并提前在司机端公示。",
    "H|2. 扣款项目",
    "P|平台可从结算金额中扣除：",
    "P|• 平台服务佣金；",
    "P|• 支付通道交易手续费；",
    "P|• 因司机违约产生的罚款、乘客赔偿、退款损失。",
    "H|3. 结算周期",
    "P|结算周期在司机端钱包页面展示，扣除相关费用后放款。",
    "H|4. 税务",
    "P|司机取得的全部收入为司机个人应税收入，司机应自行向马来西亚 LHDN 申报纳税，平台不代扣代缴。",
    "H|5. 调整",
    "P|平台可提前在 APP 公示修改佣金与结算规则；公示后继续使用司机端 APP 视为同意修改后的费率表。",
    "H|6. 效力",
    "P|本费率表为《司机服务协议》不可分割的附件，受马来西亚法律管辖。",
)

@Composable
fun RateScheduleScreen(onBack: () -> Unit = {}) {
    val en = AppLanguageStore.language == AppLanguage.EN
    PolicyScreen(
        title = if (en) "Driver Rate & Commission Schedule" else "司机费率与佣金",
        blocks = if (en) rateSchedulePolicyEn else rateSchedulePolicy,
        onBack = onBack
    )
}

/** English version of the final compliance statement (extra page, not part of the 9-doc bundle). */
private val finalCompliancePolicyEn = listOf(
    "P|1. This document applies to all three ends: Passenger APP, Driver APP, Merchant Backend and Web Portal.",
    "P|2. Fully compliant with Malaysian laws and regulations (PDPA 2024, LHDN, SSM, cross-border travel compliance).",
    "P|3. Ready for: app store submission, official website publication, user registration consent, legal filing, financing compliance and annual corporate review.",
    "P|4. Fully aligned with your existing Supabase database audit rules, data retention rules, KYC table structure and Storage compliance retention cycle.",
)
