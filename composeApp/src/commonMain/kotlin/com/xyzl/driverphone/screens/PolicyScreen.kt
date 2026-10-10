package com.xyzl.driverphone.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GoldLight = Color(0xFFE6C765)
private val BodyGray = Color(0xFFC7D4EA)

/** 《司机入驻KYC实名认证服务政策》全文页 */
@Composable
fun PolicyScreen(onBack: () -> Unit = {}) {
    val bg = Brush.verticalGradient(listOf(Color(0xFF081226), Color(0xFF0F2040), Color(0xFF102244)))
    Box(Modifier.fillMaxSize().background(bg)) {
        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, "返回", tint = Color.White)
                }
                Text("服务政策", fontSize = 22.sp, color = Color.White, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))

            PolicyTitle("星域臻旅 司机入驻KYC实名认证服务政策")
            PolicySub("（马来西亚合规版）")

            PolicyHeading("一、政策概述")
            PolicyBody(
                "为规范【星域臻旅】跨境出行司机入驻资质审核、保障平台、乘客及入驻司机合法权益，" +
                    "符合马来西亚个人资料保护法 PDPA、SSM商事合规、跨境出行监管规范，本平台针对所有申请入驻的签约司机，" +
                    "强制开启完整个人信息填报 + 真人KYC实名认证 + 资质备案审核流程。",
            )
            PolicyBody(
                "本政策适用于所有注册、入驻、接单、提供跨境客运服务的平台司机。司机提交资料、点击同意并完成KYC认证，" +
                    "即代表自愿、知情、完全认可本政策所有条款。",
            )

            PolicyHeading("二、司机必须提交的KYC认证资料（强制必填）")
            PolicyBody(
                "司机在平台注册、开通接单权限前，必须真实、完整、有效提交以下所有个人资料，" +
                    "缺项、虚假、过期一律不予通过审核、不予开通接单权限：",
            )
            PolicySubHeading("1. 基础实名信息")
            PolicyBullet("真实姓名（与证件一致）")
            PolicyBullet("性别、出生日期")
            PolicyBullet("有效马来西亚手机号（可接收验证码、真实实名开户）")
            PolicyBullet("常住地址/营运常驻区域（柔佛、吉隆坡、新山等）")
            PolicySubHeading("2. 官方有效身份证件KYC")
            PolicyBullet("马来西亚 IC身份证正反面清晰原图")
            PolicyBullet("证件必须在有效期内，无遮挡、无修图、无翻拍、无镜像")
            PolicyBullet("禁止借用他人证件、伪造证件、PS修改证件信息")
            PolicySubHeading("3. 真人活体人脸认证")
            PolicyBullet("司机本人实时活体人脸检测、眨眼、转头动态核验")
            PolicyBullet("系统留存真人人脸比对数据，用于接单抽检、人脸二次核验、防代跑单、防账号交易")
            PolicySubHeading("4. 驾驶资质证件")
            PolicyBullet("有效马来西亚驾驶执照（License）完整照片")
            PolicyBullet("驾照等级、准驾车型必须匹配平台营运车辆")
            PolicyBullet("驾照无吊销、无过期、无重大违章封禁记录")
            PolicySubHeading("5. 营运车辆备案资料")
            PolicyBullet("车辆车牌、车辆颜色、车辆型号、车辆年份")
            PolicyBullet("车辆 roadtax、保险有效凭证")
            PolicyBullet("车辆实车实拍照片（车前、车后、车内）")
            PolicySubHeading("6. 收款与财税备案信息（适配LHDN/EPF合规）")
            PolicyBullet("实名收款账户信息")
            PolicyBullet("用于平台佣金结算、打款对账、税务留存备案")

            PolicyHeading("三、KYC认证目的与使用范围")
            PolicyBody("司机明确知晓并同意：")
            PolicyNumber("1.", "所有提交资料仅用于司机资质审核、入驻备案、安全风控、订单溯源、事故追责、财税对账、平台合规存档。")
            PolicyNumber("2.", "人脸生物数据仅用于本人核验、防刷单、防虚假司机入驻，不会用于第三方商业推广。")
            PolicyNumber("3.", "跨境出行属于高监管行业，平台需留存完整司机资料用于马来西亚交通监管、SSM年审、合规抽查。")
            PolicyNumber("4.", "KYC数据将按照马来西亚 PDPA个人隐私保护法案 加密存储、权限隔离、脱敏管理。")

            PolicyHeading("四、司机承诺与资质真实性保证")
            PolicyBody("司机入驻即承诺：")
            PolicyNumber("1.", "所有提交资料真实有效、本人真实信息，无伪造、无借用、无代办、无篡改。")
            PolicyNumber("2.", "司机账号仅限本人使用，禁止租借、售卖、共享、代注册、代认证。")
            PolicyNumber("3.", "若证件过期、地址变更、驾照更新、车辆更换，司机必须 7日内主动更新KYC资料。")
            PolicyNumber("4.", "不利用平台从事非法营运、黑车载客、偷渡接送、违规跨境行为。")
            PolicyNumber("5.", "遵守马来西亚陆路交通法规、跨境出入境接送规范、平台接单服务规则。")

            PolicyHeading("五、违规处罚机制（平台强制执行）")
            PolicyBody("平台有权根据KYC审核结果及后台风控记录，执行以下处罚：")
            PolicyNumber("1.", "资料虚假、PS、伪造：永久封禁司机账号，永不解禁，保留追责权利。")
            PolicyNumber("2.", "人证不符、非本人接单：立即停止接单权限、冻结账户余额、重新强制KYC。")
            PolicyNumber("3.", "证件过期未更新：临时禁单，更新核验通过后方可恢复接单。")
            PolicyNumber("4.", "借用他人账号、交易账号、代跑单：永久拉黑、剔除平台司机库。")
            PolicyNumber("5.", "涉及非法营运、违规跨境、乘客投诉高危行为：冻结结算、上报监管、保留法律追责。")

            PolicyHeading("六、用户隐私与数据安全（PDPA合规）")
            PolicyNumber("1.", "平台严格遵循马来西亚《个人资料保护法 PDPA》，所有司机隐私数据加密存储、权限隔离、禁止外泄。")
            PolicyNumber("2.", "不向第三方出售、租借、倒卖司机个人信息、人脸数据、证件资料。")
            PolicyNumber("3.", "仅在政府监管核查、司法调查、事故溯源、合规审计场景下，依规提交备案资料。")
            PolicyNumber("4.", "司机注销账号后，平台将按法规脱敏留存合规日志、销毁可识别隐私数据。")

            PolicyHeading("七、入驻授权说明")
            PolicyBody("司机同意：")
            PolicyNumber("1.", "授权平台对本人提交的所有资料进行人工+AI双重审核。")
            PolicyNumber("2.", "授权平台留存人脸、证件、车辆资料用于长期风控与合规备案。")
            PolicyNumber("3.", "授权平台根据营运规则进行不定期二次复审KYC。")
            PolicyNumber("4.", "授权平台依据违规规则执行账号封禁、冻结、清退操作。")

            PolicyHeading("八、政策更新与生效")
            PolicyNumber("1.", "本《司机KYC实名认证入驻政策》将根据马来西亚交通合规、PDPA法案、平台规则随时更新。")
            PolicyNumber("2.", "更新后无需单独通知，司机持续登录、接单即代表默认接受最新条款。")
            PolicyNumber("3.", "本政策自司机注册KYC页面展示之日起正式生效。")

            PolicyHeading("九、最终解释权")
            PolicyBody(
                "本政策最终解释权归【星域臻旅 / Stellar Tech Studio】所有，所有司机入驻、审核、风控、处罚、" +
                    "合规事宜均以本条款为唯一依据。",
            )

            Spacer(Modifier.height(16.dp))
            PolicyBody("星域臻旅 合规运营部")
            PolicyBody("马来西亚SSM合规备案 · PDPA隐私合规")
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun PolicyTitle(text: String) {
    Text(text, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 28.sp)
}

@Composable
private fun PolicySub(text: String) {
    Text(text, color = GoldLight, fontSize = 13.sp)
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun PolicyHeading(text: String) {
    Spacer(Modifier.height(14.dp))
    Text(text, color = GoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun PolicySubHeading(text: String) {
    Spacer(Modifier.height(6.dp))
    Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(4.dp))
}

@Composable
private fun PolicyBody(text: String) {
    Text(text, color = BodyGray, fontSize = 13.sp, lineHeight = 22.sp)
    Spacer(Modifier.height(6.dp))
}

@Composable
private fun PolicyBullet(text: String) {
    Row(Modifier.fillMaxWidth().padding(start = 4.dp)) {
        Text("• ", color = GoldLight, fontSize = 13.sp, lineHeight = 22.sp)
        Text(text, color = BodyGray, fontSize = 13.sp, lineHeight = 22.sp)
    }
    Spacer(Modifier.height(2.dp))
}

@Composable
private fun PolicyNumber(num: String, text: String) {
    Row(Modifier.fillMaxWidth().padding(start = 4.dp)) {
        Text("$num ", color = GoldLight, fontSize = 13.sp, lineHeight = 22.sp)
        Text(text, color = BodyGray, fontSize = 13.sp, lineHeight = 22.sp)
    }
    Spacer(Modifier.height(4.dp))
}
