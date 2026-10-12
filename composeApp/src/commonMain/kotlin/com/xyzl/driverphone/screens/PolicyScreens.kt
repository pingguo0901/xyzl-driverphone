package com.xyzl.driverphone.screens

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
    PolicyScreen(title = "用户服务协议", blocks = userServicePolicy, onBack = onBack)
}

@Composable
fun PrivacyPolicyScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "隐私政策", blocks = privacyPolicy, onBack = onBack)
}

@Composable
fun KycPolicyScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "KYC实名认证合规条款", blocks = kycPolicy, onBack = onBack)
}

@Composable
fun RefundPolicyScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "退款与交易合规政策", blocks = refundPolicy, onBack = onBack)
}

@Composable
fun DataRetentionPolicyScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "数据留存与审计合规政策", blocks = dataRetentionPolicy, onBack = onBack)
}

@Composable
fun DisclaimerScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "免责声明", blocks = disclaimerPolicy, onBack = onBack)
}

@Composable
fun FinalComplianceScreen(onBack: () -> Unit = {}) {
    PolicyScreen(title = "合规最终说明", blocks = finalCompliancePolicy, onBack = onBack)
}
