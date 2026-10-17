package com.xyzl.driverphone

import androidx.compose.runtime.staticCompositionLocalOf

/** 全局文案字典：所有 UI 静态文本都从这里取；%%s 用 fill(...) 填。 */
fun String.fill(vararg args: Any?): String {
    if (args.isEmpty()) return this
    var i = 0
    return Regex("%s").replace(this) { m -> val v = args.getOrNull(i++)?.toString() ?: ""; v }
}

class AppStrings(private val v: Map<String, String>) {
    val back: String get() = v["back"] ?: ""
    val cancel: String get() = v["cancel"] ?: ""
    val chat: String get() = v["chat"] ?: ""
    val chatRecords: String get() = v["chatRecords"] ?: ""
    val clear: String get() = v["clear"] ?: ""
    val clearChats: String get() = v["clearChats"] ?: ""
    val clearedText: String get() = v["clearedText"] ?: ""
    val clearedTitle: String get() = v["clearedTitle"] ?: ""
    val confirm: String get() = v["confirm"] ?: ""
    val display: String get() = v["display"] ?: ""
    val done: String get() = v["done"] ?: ""
    val exportChats: String get() = v["exportChats"] ?: ""
    val exportedText: String get() = v["exportedText"] ?: ""
    val exportedTitle: String get() = v["exportedTitle"] ?: ""
    val failed: String get() = v["failed"] ?: ""
    val fontSize: String get() = v["fontSize"] ?: ""
    val language: String get() = v["language"] ?: ""
    val notifications: String get() = v["notifications"] ?: ""
    val s0002: String get() = v["s0002"] ?: ""
    val s0004: String get() = v["s0004"] ?: ""
    val s0005: String get() = v["s0005"] ?: ""
    val s0006: String get() = v["s0006"] ?: ""
    val s0007: String get() = v["s0007"] ?: ""
    val s0008: String get() = v["s0008"] ?: ""
    val s0009: String get() = v["s0009"] ?: ""
    val s0010: String get() = v["s0010"] ?: ""
    val s0011: String get() = v["s0011"] ?: ""
    val s0012: String get() = v["s0012"] ?: ""
    val s0013: String get() = v["s0013"] ?: ""
    val s0014: String get() = v["s0014"] ?: ""
    val s0015: String get() = v["s0015"] ?: ""
    val s0016: String get() = v["s0016"] ?: ""
    val s0017: String get() = v["s0017"] ?: ""
    val s0018: String get() = v["s0018"] ?: ""
    val s0019: String get() = v["s0019"] ?: ""
    val s0020: String get() = v["s0020"] ?: ""
    val s0021: String get() = v["s0021"] ?: ""
    val s0022: String get() = v["s0022"] ?: ""
    val s0023: String get() = v["s0023"] ?: ""
    val s0024: String get() = v["s0024"] ?: ""
    val s0025: String get() = v["s0025"] ?: ""
    val s0026: String get() = v["s0026"] ?: ""
    val s0027: String get() = v["s0027"] ?: ""
    val s0028: String get() = v["s0028"] ?: ""
    val s0029: String get() = v["s0029"] ?: ""
    val s0030: String get() = v["s0030"] ?: ""
    val s0031: String get() = v["s0031"] ?: ""
    val s0032: String get() = v["s0032"] ?: ""
    val s0033: String get() = v["s0033"] ?: ""
    val s0034: String get() = v["s0034"] ?: ""
    val s0035: String get() = v["s0035"] ?: ""
    val s0036: String get() = v["s0036"] ?: ""
    val s0037: String get() = v["s0037"] ?: ""
    val s0038: String get() = v["s0038"] ?: ""
    val s0039: String get() = v["s0039"] ?: ""
    val s0040: String get() = v["s0040"] ?: ""
    val s0041: String get() = v["s0041"] ?: ""
    val s0042: String get() = v["s0042"] ?: ""
    val s0044: String get() = v["s0044"] ?: ""
    val s0045: String get() = v["s0045"] ?: ""
    val s0046: String get() = v["s0046"] ?: ""
    val s0047: String get() = v["s0047"] ?: ""
    val s0048: String get() = v["s0048"] ?: ""
    val s0049: String get() = v["s0049"] ?: ""
    val s0050: String get() = v["s0050"] ?: ""
    val s0051: String get() = v["s0051"] ?: ""
    val s0052: String get() = v["s0052"] ?: ""
    val s0053: String get() = v["s0053"] ?: ""
    val s0054: String get() = v["s0054"] ?: ""
    val s0055: String get() = v["s0055"] ?: ""
    val s0056: String get() = v["s0056"] ?: ""
    val s0057: String get() = v["s0057"] ?: ""
    val s0058: String get() = v["s0058"] ?: ""
    val s0059: String get() = v["s0059"] ?: ""
    val s0060: String get() = v["s0060"] ?: ""
    val s0061: String get() = v["s0061"] ?: ""
    val s0062: String get() = v["s0062"] ?: ""
    val s0063: String get() = v["s0063"] ?: ""
    val s0064: String get() = v["s0064"] ?: ""
    val s0065: String get() = v["s0065"] ?: ""
    val s0066: String get() = v["s0066"] ?: ""
    val s0067: String get() = v["s0067"] ?: ""
    val s0068: String get() = v["s0068"] ?: ""
    val s0069: String get() = v["s0069"] ?: ""
    val s0070: String get() = v["s0070"] ?: ""
    val s0071: String get() = v["s0071"] ?: ""
    val s0072: String get() = v["s0072"] ?: ""
    val s0073: String get() = v["s0073"] ?: ""
    val s0076: String get() = v["s0076"] ?: ""
    val s0077: String get() = v["s0077"] ?: ""
    val s0078: String get() = v["s0078"] ?: ""
    val s0079: String get() = v["s0079"] ?: ""
    val s0080: String get() = v["s0080"] ?: ""
    val s0081: String get() = v["s0081"] ?: ""
    val s0082: String get() = v["s0082"] ?: ""
    val s0083: String get() = v["s0083"] ?: ""
    val s0084: String get() = v["s0084"] ?: ""
    val s0085: String get() = v["s0085"] ?: ""
    val s0086: String get() = v["s0086"] ?: ""
    val s0087: String get() = v["s0087"] ?: ""
    val s0088: String get() = v["s0088"] ?: ""
    val s0089: String get() = v["s0089"] ?: ""
    val s0090: String get() = v["s0090"] ?: ""
    val s0091: String get() = v["s0091"] ?: ""
    val s0092: String get() = v["s0092"] ?: ""
    val s0093: String get() = v["s0093"] ?: ""
    val s0094: String get() = v["s0094"] ?: ""
    val s0095: String get() = v["s0095"] ?: ""
    val s0096: String get() = v["s0096"] ?: ""
    val s0097: String get() = v["s0097"] ?: ""
    val s0098: String get() = v["s0098"] ?: ""
    val s0099: String get() = v["s0099"] ?: ""
    val s0100: String get() = v["s0100"] ?: ""
    val s0101: String get() = v["s0101"] ?: ""
    val s0102: String get() = v["s0102"] ?: ""
    val s0103: String get() = v["s0103"] ?: ""
    val s0104: String get() = v["s0104"] ?: ""
    val s0105: String get() = v["s0105"] ?: ""
    val s0106: String get() = v["s0106"] ?: ""
    val s0107: String get() = v["s0107"] ?: ""
    val s0108: String get() = v["s0108"] ?: ""
    val s0109: String get() = v["s0109"] ?: ""
    val s0110: String get() = v["s0110"] ?: ""
    val s0111: String get() = v["s0111"] ?: ""
    val s0112: String get() = v["s0112"] ?: ""
    val s0113: String get() = v["s0113"] ?: ""
    val s0114: String get() = v["s0114"] ?: ""
    val s0115: String get() = v["s0115"] ?: ""
    val s0116: String get() = v["s0116"] ?: ""
    val s0117: String get() = v["s0117"] ?: ""
    val s0118: String get() = v["s0118"] ?: ""
    val s0119: String get() = v["s0119"] ?: ""
    val s0120: String get() = v["s0120"] ?: ""
    val s0121: String get() = v["s0121"] ?: ""
    val s0122: String get() = v["s0122"] ?: ""
    val s0123: String get() = v["s0123"] ?: ""
    val s0124: String get() = v["s0124"] ?: ""
    val s0125: String get() = v["s0125"] ?: ""
    val s0126: String get() = v["s0126"] ?: ""
    val s0127: String get() = v["s0127"] ?: ""
    val s0128: String get() = v["s0128"] ?: ""
    val s0129: String get() = v["s0129"] ?: ""
    val s0130: String get() = v["s0130"] ?: ""
    val s0131: String get() = v["s0131"] ?: ""
    val s0132: String get() = v["s0132"] ?: ""
    val s0133: String get() = v["s0133"] ?: ""
    val s0134: String get() = v["s0134"] ?: ""
    val s0135: String get() = v["s0135"] ?: ""
    val s0136: String get() = v["s0136"] ?: ""
    val s0137: String get() = v["s0137"] ?: ""
    val s0138: String get() = v["s0138"] ?: ""
    val s0139: String get() = v["s0139"] ?: ""
    val s0140: String get() = v["s0140"] ?: ""
    val s0141: String get() = v["s0141"] ?: ""
    val s0142: String get() = v["s0142"] ?: ""
    val s0143: String get() = v["s0143"] ?: ""
    val s0144: String get() = v["s0144"] ?: ""
    val s0145: String get() = v["s0145"] ?: ""
    val s0146: String get() = v["s0146"] ?: ""
    val s0147: String get() = v["s0147"] ?: ""
    val s0148: String get() = v["s0148"] ?: ""
    val s0149: String get() = v["s0149"] ?: ""
    val s0150: String get() = v["s0150"] ?: ""
    val s0151: String get() = v["s0151"] ?: ""
    val s0152: String get() = v["s0152"] ?: ""
    val s0153: String get() = v["s0153"] ?: ""
    val s0154: String get() = v["s0154"] ?: ""
    val s0155: String get() = v["s0155"] ?: ""
    val s0156: String get() = v["s0156"] ?: ""
    val s0157: String get() = v["s0157"] ?: ""
    val s0158: String get() = v["s0158"] ?: ""
    val s0159: String get() = v["s0159"] ?: ""
    val s0160: String get() = v["s0160"] ?: ""
    val s0161: String get() = v["s0161"] ?: ""
    val s0162: String get() = v["s0162"] ?: ""
    val s0163: String get() = v["s0163"] ?: ""
    val s0164: String get() = v["s0164"] ?: ""
    val s0165: String get() = v["s0165"] ?: ""
    val s0166: String get() = v["s0166"] ?: ""
    val s0167: String get() = v["s0167"] ?: ""
    val s0168: String get() = v["s0168"] ?: ""
    val s0169: String get() = v["s0169"] ?: ""
    val s0170: String get() = v["s0170"] ?: ""
    val s0171: String get() = v["s0171"] ?: ""
    val s0172: String get() = v["s0172"] ?: ""
    val s0173: String get() = v["s0173"] ?: ""
    val s0174: String get() = v["s0174"] ?: ""
    val s0175: String get() = v["s0175"] ?: ""
    val s0176: String get() = v["s0176"] ?: ""
    val s0177: String get() = v["s0177"] ?: ""
    val s0178: String get() = v["s0178"] ?: ""
    val s0179: String get() = v["s0179"] ?: ""
    val s0180: String get() = v["s0180"] ?: ""
    val s0181: String get() = v["s0181"] ?: ""
    val s0182: String get() = v["s0182"] ?: ""
    val s0183: String get() = v["s0183"] ?: ""
    val s0184: String get() = v["s0184"] ?: ""
    val s0185: String get() = v["s0185"] ?: ""
    val s0186: String get() = v["s0186"] ?: ""
    val s0187: String get() = v["s0187"] ?: ""
    val s0188: String get() = v["s0188"] ?: ""
    val s0189: String get() = v["s0189"] ?: ""
    val s0190: String get() = v["s0190"] ?: ""
    val s0191: String get() = v["s0191"] ?: ""
    val s0192: String get() = v["s0192"] ?: ""
    val s0193: String get() = v["s0193"] ?: ""
    val s0194: String get() = v["s0194"] ?: ""
    val s0195: String get() = v["s0195"] ?: ""
    val s0196: String get() = v["s0196"] ?: ""
    val s0197: String get() = v["s0197"] ?: ""
    val s0198: String get() = v["s0198"] ?: ""
    val s0199: String get() = v["s0199"] ?: ""
    val s0200: String get() = v["s0200"] ?: ""
    val s0201: String get() = v["s0201"] ?: ""
    val s0202: String get() = v["s0202"] ?: ""
    val s0203: String get() = v["s0203"] ?: ""
    val s0204: String get() = v["s0204"] ?: ""
    val s0205: String get() = v["s0205"] ?: ""
    val s0206: String get() = v["s0206"] ?: ""
    val s0207: String get() = v["s0207"] ?: ""
    val s0208: String get() = v["s0208"] ?: ""
    val s0209: String get() = v["s0209"] ?: ""
    val s0210: String get() = v["s0210"] ?: ""
    val s0211: String get() = v["s0211"] ?: ""
    val s0212: String get() = v["s0212"] ?: ""
    val s0213: String get() = v["s0213"] ?: ""
    val s0214: String get() = v["s0214"] ?: ""
    val s0215: String get() = v["s0215"] ?: ""
    val s0216: String get() = v["s0216"] ?: ""
    val s0217: String get() = v["s0217"] ?: ""
    val s0218: String get() = v["s0218"] ?: ""
    val s0219: String get() = v["s0219"] ?: ""
    val s0220: String get() = v["s0220"] ?: ""
    val s0221: String get() = v["s0221"] ?: ""
    val s0222: String get() = v["s0222"] ?: ""
    val s0223: String get() = v["s0223"] ?: ""
    val s0224: String get() = v["s0224"] ?: ""
    val s0225: String get() = v["s0225"] ?: ""
    val s0226: String get() = v["s0226"] ?: ""
    val s0227: String get() = v["s0227"] ?: ""
    val s0228: String get() = v["s0228"] ?: ""
    val s0229: String get() = v["s0229"] ?: ""
    val s0230: String get() = v["s0230"] ?: ""
    val s0231: String get() = v["s0231"] ?: ""
    val s0232: String get() = v["s0232"] ?: ""
    val s0233: String get() = v["s0233"] ?: ""
    val s0234: String get() = v["s0234"] ?: ""
    val s0235: String get() = v["s0235"] ?: ""
    val s0236: String get() = v["s0236"] ?: ""
    val s0237: String get() = v["s0237"] ?: ""
    val s0238: String get() = v["s0238"] ?: ""
    val s0239: String get() = v["s0239"] ?: ""
    val s0240: String get() = v["s0240"] ?: ""
    val s0241: String get() = v["s0241"] ?: ""
    val s0242: String get() = v["s0242"] ?: ""
    val s0243: String get() = v["s0243"] ?: ""
    val s0244: String get() = v["s0244"] ?: ""
    val s0245: String get() = v["s0245"] ?: ""
    val s0246: String get() = v["s0246"] ?: ""
    val s0247: String get() = v["s0247"] ?: ""
    val s0248: String get() = v["s0248"] ?: ""
    val s0249: String get() = v["s0249"] ?: ""
    val s0250: String get() = v["s0250"] ?: ""
    val s0251: String get() = v["s0251"] ?: ""
    val s0252: String get() = v["s0252"] ?: ""
    val s0253: String get() = v["s0253"] ?: ""
    val s0254: String get() = v["s0254"] ?: ""
    val s0255: String get() = v["s0255"] ?: ""
    val s0256: String get() = v["s0256"] ?: ""
    val s0257: String get() = v["s0257"] ?: ""
    val s0258: String get() = v["s0258"] ?: ""
    val s0259: String get() = v["s0259"] ?: ""
    val s0260: String get() = v["s0260"] ?: ""
    val s0261: String get() = v["s0261"] ?: ""
    val s0262: String get() = v["s0262"] ?: ""
    val s0263: String get() = v["s0263"] ?: ""
    val s0264: String get() = v["s0264"] ?: ""
    val s0265: String get() = v["s0265"] ?: ""
    val s0266: String get() = v["s0266"] ?: ""
    val s0267: String get() = v["s0267"] ?: ""
    val s0268: String get() = v["s0268"] ?: ""
    val s0269: String get() = v["s0269"] ?: ""
    val s0270: String get() = v["s0270"] ?: ""
    val s0271: String get() = v["s0271"] ?: ""
    val s0272: String get() = v["s0272"] ?: ""
    val s0273: String get() = v["s0273"] ?: ""
    val s0274: String get() = v["s0274"] ?: ""
    val s0275: String get() = v["s0275"] ?: ""
    val s0276: String get() = v["s0276"] ?: ""
    val s0277: String get() = v["s0277"] ?: ""
    val s0278: String get() = v["s0278"] ?: ""
    val s0279: String get() = v["s0279"] ?: ""
    val s0280: String get() = v["s0280"] ?: ""
    val s0281: String get() = v["s0281"] ?: ""
    val s0282: String get() = v["s0282"] ?: ""
    val s0283: String get() = v["s0283"] ?: ""
    val s0284: String get() = v["s0284"] ?: ""
    val s0285: String get() = v["s0285"] ?: ""
    val s0286: String get() = v["s0286"] ?: ""
    val s0287: String get() = v["s0287"] ?: ""
    val s0288: String get() = v["s0288"] ?: ""
    val s0289: String get() = v["s0289"] ?: ""
    val s0290: String get() = v["s0290"] ?: ""
    val s0291: String get() = v["s0291"] ?: ""
    val s0292: String get() = v["s0292"] ?: ""
    val s0293: String get() = v["s0293"] ?: ""
    val s0294: String get() = v["s0294"] ?: ""
    val s0295: String get() = v["s0295"] ?: ""
    val s0296: String get() = v["s0296"] ?: ""
    val s0297: String get() = v["s0297"] ?: ""
    val s0298: String get() = v["s0298"] ?: ""
    val s0299: String get() = v["s0299"] ?: ""
    val s0300: String get() = v["s0300"] ?: ""
    val s0301: String get() = v["s0301"] ?: ""
    val s0302: String get() = v["s0302"] ?: ""
    val s0303: String get() = v["s0303"] ?: ""
    val s0304: String get() = v["s0304"] ?: ""
    val s0305: String get() = v["s0305"] ?: ""
    val s0306: String get() = v["s0306"] ?: ""
    val s0307: String get() = v["s0307"] ?: ""
    val s0308: String get() = v["s0308"] ?: ""
    val s0309: String get() = v["s0309"] ?: ""
    val s0310: String get() = v["s0310"] ?: ""
    val s0311: String get() = v["s0311"] ?: ""
    val s0312: String get() = v["s0312"] ?: ""
    val s0313: String get() = v["s0313"] ?: ""
    val s0314: String get() = v["s0314"] ?: ""
    val s0315: String get() = v["s0315"] ?: ""
    val s0316: String get() = v["s0316"] ?: ""
    val s0317: String get() = v["s0317"] ?: ""
    val s0318: String get() = v["s0318"] ?: ""
    val s0319: String get() = v["s0319"] ?: ""
    val s0320: String get() = v["s0320"] ?: ""
    val s0321: String get() = v["s0321"] ?: ""
    val s0322: String get() = v["s0322"] ?: ""
    val s0323: String get() = v["s0323"] ?: ""
    val s0324: String get() = v["s0324"] ?: ""
    val s0325: String get() = v["s0325"] ?: ""
    val s0326: String get() = v["s0326"] ?: ""
    val s0327: String get() = v["s0327"] ?: ""
    val s0328: String get() = v["s0328"] ?: ""
    val s0329: String get() = v["s0329"] ?: ""
    val s0330: String get() = v["s0330"] ?: ""
    val s0331: String get() = v["s0331"] ?: ""
    val s0332: String get() = v["s0332"] ?: ""
    val s0333: String get() = v["s0333"] ?: ""
    val s0334: String get() = v["s0334"] ?: ""
    val s0335: String get() = v["s0335"] ?: ""
    val s0336: String get() = v["s0336"] ?: ""
    val s0337: String get() = v["s0337"] ?: ""
    val s0338: String get() = v["s0338"] ?: ""
    val s0339: String get() = v["s0339"] ?: ""
    val s0340: String get() = v["s0340"] ?: ""
    val s0341: String get() = v["s0341"] ?: ""
    val s0342: String get() = v["s0342"] ?: ""
    val s0343: String get() = v["s0343"] ?: ""
    val s0344: String get() = v["s0344"] ?: ""
    val s0345: String get() = v["s0345"] ?: ""
    val s0346: String get() = v["s0346"] ?: ""
    val s0347: String get() = v["s0347"] ?: ""
    val s0348: String get() = v["s0348"] ?: ""
    val s0349: String get() = v["s0349"] ?: ""
    val s0350: String get() = v["s0350"] ?: ""
    val s0351: String get() = v["s0351"] ?: ""
    val s0352: String get() = v["s0352"] ?: ""
    val s0353: String get() = v["s0353"] ?: ""
    val s0354: String get() = v["s0354"] ?: ""
    val s0355: String get() = v["s0355"] ?: ""
    val s0356: String get() = v["s0356"] ?: ""
    val s0357: String get() = v["s0357"] ?: ""
    val s0358: String get() = v["s0358"] ?: ""
    val s0359: String get() = v["s0359"] ?: ""
    val s0360: String get() = v["s0360"] ?: ""
    val s0361: String get() = v["s0361"] ?: ""
    val s0362: String get() = v["s0362"] ?: ""
    val s0363: String get() = v["s0363"] ?: ""
    val s0364: String get() = v["s0364"] ?: ""
    val s0365: String get() = v["s0365"] ?: ""
    val s0366: String get() = v["s0366"] ?: ""
    val s0367: String get() = v["s0367"] ?: ""
    val s0368: String get() = v["s0368"] ?: ""
    val s0369: String get() = v["s0369"] ?: ""
    val s0370: String get() = v["s0370"] ?: ""
    val s0371: String get() = v["s0371"] ?: ""
    val s0372: String get() = v["s0372"] ?: ""
    val s0373: String get() = v["s0373"] ?: ""
    val s0374: String get() = v["s0374"] ?: ""
    val s0375: String get() = v["s0375"] ?: ""
    val s0376: String get() = v["s0376"] ?: ""
    val s0377: String get() = v["s0377"] ?: ""
    val s0378: String get() = v["s0378"] ?: ""
    val s0379: String get() = v["s0379"] ?: ""
    val s0380: String get() = v["s0380"] ?: ""
    val s0381: String get() = v["s0381"] ?: ""
    val s0382: String get() = v["s0382"] ?: ""
    val s0383: String get() = v["s0383"] ?: ""
    val s0384: String get() = v["s0384"] ?: ""
    val s0385: String get() = v["s0385"] ?: ""
    val s0386: String get() = v["s0386"] ?: ""
    val s0387: String get() = v["s0387"] ?: ""
    val s0388: String get() = v["s0388"] ?: ""
    val s0389: String get() = v["s0389"] ?: ""
    val s0390: String get() = v["s0390"] ?: ""
    val s0391: String get() = v["s0391"] ?: ""
    val s0392: String get() = v["s0392"] ?: ""
    val s0393: String get() = v["s0393"] ?: ""
    val s0394: String get() = v["s0394"] ?: ""
    val s0395: String get() = v["s0395"] ?: ""
    val s0396: String get() = v["s0396"] ?: ""
    val s0397: String get() = v["s0397"] ?: ""
    val s0398: String get() = v["s0398"] ?: ""
    val s0399: String get() = v["s0399"] ?: ""
    val s0400: String get() = v["s0400"] ?: ""
    val s0401: String get() = v["s0401"] ?: ""
    val s0402: String get() = v["s0402"] ?: ""
    val s0403: String get() = v["s0403"] ?: ""
    val s0404: String get() = v["s0404"] ?: ""
    val s0405: String get() = v["s0405"] ?: ""
    val s0406: String get() = v["s0406"] ?: ""
    val s0407: String get() = v["s0407"] ?: ""
    val s0408: String get() = v["s0408"] ?: ""
    val s0409: String get() = v["s0409"] ?: ""
    val s0410: String get() = v["s0410"] ?: ""
    val s0411: String get() = v["s0411"] ?: ""
    val s0412: String get() = v["s0412"] ?: ""
    val s0413: String get() = v["s0413"] ?: ""
    val s0414: String get() = v["s0414"] ?: ""
    val s0415: String get() = v["s0415"] ?: ""
    val s0416: String get() = v["s0416"] ?: ""
    val s0417: String get() = v["s0417"] ?: ""
    val s0418: String get() = v["s0418"] ?: ""
    val s0419: String get() = v["s0419"] ?: ""
    val s0420: String get() = v["s0420"] ?: ""
    val s0421: String get() = v["s0421"] ?: ""
    val s0422: String get() = v["s0422"] ?: ""
    val s0423: String get() = v["s0423"] ?: ""
    val s0424: String get() = v["s0424"] ?: ""
    val s0425: String get() = v["s0425"] ?: ""
    val s0426: String get() = v["s0426"] ?: ""
    val s0427: String get() = v["s0427"] ?: ""
    val s0428: String get() = v["s0428"] ?: ""
    val s0429: String get() = v["s0429"] ?: ""
    val s0430: String get() = v["s0430"] ?: ""
    val s0431: String get() = v["s0431"] ?: ""
    val s0432: String get() = v["s0432"] ?: ""
    val s0433: String get() = v["s0433"] ?: ""
    val s0434: String get() = v["s0434"] ?: ""
    val s0435: String get() = v["s0435"] ?: ""
    val s0436: String get() = v["s0436"] ?: ""
    val s0437: String get() = v["s0437"] ?: ""
    val s0438: String get() = v["s0438"] ?: ""
    val s0439: String get() = v["s0439"] ?: ""
    val s0440: String get() = v["s0440"] ?: ""
    val s0441: String get() = v["s0441"] ?: ""
    val s0442: String get() = v["s0442"] ?: ""
    val s0443: String get() = v["s0443"] ?: ""
    val s0444: String get() = v["s0444"] ?: ""
    val s0445: String get() = v["s0445"] ?: ""
    val s0446: String get() = v["s0446"] ?: ""
    val s0447: String get() = v["s0447"] ?: ""
    val s0448: String get() = v["s0448"] ?: ""
    val s0449: String get() = v["s0449"] ?: ""
    val s0450: String get() = v["s0450"] ?: ""
    val s0451: String get() = v["s0451"] ?: ""
    val s0452: String get() = v["s0452"] ?: ""
    val s0453: String get() = v["s0453"] ?: ""
    val s0454: String get() = v["s0454"] ?: ""
    val s0455: String get() = v["s0455"] ?: ""
    val s0456: String get() = v["s0456"] ?: ""
    val s0457: String get() = v["s0457"] ?: ""
    val s0458: String get() = v["s0458"] ?: ""
    val s0459: String get() = v["s0459"] ?: ""
    val s0460: String get() = v["s0460"] ?: ""
    val s0461: String get() = v["s0461"] ?: ""
    val s0462: String get() = v["s0462"] ?: ""
    val s0463: String get() = v["s0463"] ?: ""
    val s0464: String get() = v["s0464"] ?: ""
    val s0465: String get() = v["s0465"] ?: ""
    val s0466: String get() = v["s0466"] ?: ""
    val s0467: String get() = v["s0467"] ?: ""
    val s0468: String get() = v["s0468"] ?: ""
    val s0469: String get() = v["s0469"] ?: ""
    val s0470: String get() = v["s0470"] ?: ""
    val s0471: String get() = v["s0471"] ?: ""
    val s0472: String get() = v["s0472"] ?: ""
    val s0473: String get() = v["s0473"] ?: ""
    val s0474: String get() = v["s0474"] ?: ""
    val s0475: String get() = v["s0475"] ?: ""
    val s0476: String get() = v["s0476"] ?: ""
    val s0477: String get() = v["s0477"] ?: ""
    val s0478: String get() = v["s0478"] ?: ""
    val s0479: String get() = v["s0479"] ?: ""
    val s0480: String get() = v["s0480"] ?: ""
    val s0481: String get() = v["s0481"] ?: ""
    val s0482: String get() = v["s0482"] ?: ""
    val s0483: String get() = v["s0483"] ?: ""
    val s0484: String get() = v["s0484"] ?: ""
    val s0485: String get() = v["s0485"] ?: ""
    val s0486: String get() = v["s0486"] ?: ""
    val s0487: String get() = v["s0487"] ?: ""
    val s0488: String get() = v["s0488"] ?: ""
    val s0489: String get() = v["s0489"] ?: ""
    val s0490: String get() = v["s0490"] ?: ""
    val s0491: String get() = v["s0491"] ?: ""
    val s0492: String get() = v["s0492"] ?: ""
    val s0493: String get() = v["s0493"] ?: ""
    val s0494: String get() = v["s0494"] ?: ""
    val s0495: String get() = v["s0495"] ?: ""
    val s0496: String get() = v["s0496"] ?: ""
    val s0497: String get() = v["s0497"] ?: ""
    val s0498: String get() = v["s0498"] ?: ""
    val s0499: String get() = v["s0499"] ?: ""
    val s0500: String get() = v["s0500"] ?: ""
    val s0501: String get() = v["s0501"] ?: ""
    val s0502: String get() = v["s0502"] ?: ""
    val s0503: String get() = v["s0503"] ?: ""
    val s0504: String get() = v["s0504"] ?: ""
    val s0505: String get() = v["s0505"] ?: ""
    val s0506: String get() = v["s0506"] ?: ""
    val s0507: String get() = v["s0507"] ?: ""
    val s0508: String get() = v["s0508"] ?: ""
    val s0509: String get() = v["s0509"] ?: ""
    val s0510: String get() = v["s0510"] ?: ""
    val s0511: String get() = v["s0511"] ?: ""
    val s0512: String get() = v["s0512"] ?: ""
    val s0513: String get() = v["s0513"] ?: ""
    val s0514: String get() = v["s0514"] ?: ""
    val s0515: String get() = v["s0515"] ?: ""
    val s0516: String get() = v["s0516"] ?: ""
    val s0517: String get() = v["s0517"] ?: ""
    val s0518: String get() = v["s0518"] ?: ""
    val s0519: String get() = v["s0519"] ?: ""
    val s0520: String get() = v["s0520"] ?: ""
    val s0521: String get() = v["s0521"] ?: ""
    val s0522: String get() = v["s0522"] ?: ""
    val s0523: String get() = v["s0523"] ?: ""
    val s0524: String get() = v["s0524"] ?: ""
    val s0525: String get() = v["s0525"] ?: ""
    val s0526: String get() = v["s0526"] ?: ""
    val s0527: String get() = v["s0527"] ?: ""
    val s0528: String get() = v["s0528"] ?: ""
    val s0529: String get() = v["s0529"] ?: ""
    val s0530: String get() = v["s0530"] ?: ""
    val s0531: String get() = v["s0531"] ?: ""
    val s0532: String get() = v["s0532"] ?: ""
    val s0533: String get() = v["s0533"] ?: ""
    val s0534: String get() = v["s0534"] ?: ""
    val s0535: String get() = v["s0535"] ?: ""
    val s0536: String get() = v["s0536"] ?: ""
    val s0537: String get() = v["s0537"] ?: ""
    val s0538: String get() = v["s0538"] ?: ""
    val s0539: String get() = v["s0539"] ?: ""
    val s0540: String get() = v["s0540"] ?: ""
    val s0541: String get() = v["s0541"] ?: ""
    val s0542: String get() = v["s0542"] ?: ""
    val s0543: String get() = v["s0543"] ?: ""
    val s0544: String get() = v["s0544"] ?: ""
    val s0545: String get() = v["s0545"] ?: ""
    val s0546: String get() = v["s0546"] ?: ""
    val s0547: String get() = v["s0547"] ?: ""
    val s0548: String get() = v["s0548"] ?: ""
    val s0549: String get() = v["s0549"] ?: ""
    val s0550: String get() = v["s0550"] ?: ""
    val s0551: String get() = v["s0551"] ?: ""
    val s0552: String get() = v["s0552"] ?: ""
    val s0553: String get() = v["s0553"] ?: ""
    val s0554: String get() = v["s0554"] ?: ""
    val s0555: String get() = v["s0555"] ?: ""
    val s0556: String get() = v["s0556"] ?: ""
    val s0557: String get() = v["s0557"] ?: ""
    val s0558: String get() = v["s0558"] ?: ""
    val s0559: String get() = v["s0559"] ?: ""
    val s0560: String get() = v["s0560"] ?: ""
    val s0561: String get() = v["s0561"] ?: ""
    val s0562: String get() = v["s0562"] ?: ""
    val s0563: String get() = v["s0563"] ?: ""
    val s0564: String get() = v["s0564"] ?: ""
    val s0565: String get() = v["s0565"] ?: ""
    val s0566: String get() = v["s0566"] ?: ""
    val s0567: String get() = v["s0567"] ?: ""
    val s0568: String get() = v["s0568"] ?: ""
    val s0569: String get() = v["s0569"] ?: ""
    val s0570: String get() = v["s0570"] ?: ""
    val s0571: String get() = v["s0571"] ?: ""
    val s0572: String get() = v["s0572"] ?: ""
    val s0573: String get() = v["s0573"] ?: ""
    val s0574: String get() = v["s0574"] ?: ""
    val s0575: String get() = v["s0575"] ?: ""
    val s0576: String get() = v["s0576"] ?: ""
    val s0577: String get() = v["s0577"] ?: ""
    val s0578: String get() = v["s0578"] ?: ""
    val s0579: String get() = v["s0579"] ?: ""
    val s0580: String get() = v["s0580"] ?: ""
    val s0581: String get() = v["s0581"] ?: ""
    val s0582: String get() = v["s0582"] ?: ""
    val s0583: String get() = v["s0583"] ?: ""
    val s0584: String get() = v["s0584"] ?: ""
    val s0585: String get() = v["s0585"] ?: ""
    val s0586: String get() = v["s0586"] ?: ""
    val s0587: String get() = v["s0587"] ?: ""
    val s0588: String get() = v["s0588"] ?: ""
    val s0589: String get() = v["s0589"] ?: ""
    val s0590: String get() = v["s0590"] ?: ""
    val s0591: String get() = v["s0591"] ?: ""
    val s0592: String get() = v["s0592"] ?: ""
    val s0593: String get() = v["s0593"] ?: ""
    val s0594: String get() = v["s0594"] ?: ""
    val s0595: String get() = v["s0595"] ?: ""
    val s0596: String get() = v["s0596"] ?: ""
    val s0597: String get() = v["s0597"] ?: ""
    val s0598: String get() = v["s0598"] ?: ""
    val s0599: String get() = v["s0599"] ?: ""
    val s0600: String get() = v["s0600"] ?: ""
    val s0601: String get() = v["s0601"] ?: ""
    val s0602: String get() = v["s0602"] ?: ""
    val s0603: String get() = v["s0603"] ?: ""
    val s0604: String get() = v["s0604"] ?: ""
    val s0605: String get() = v["s0605"] ?: ""
    val s0606: String get() = v["s0606"] ?: ""
    val s0607: String get() = v["s0607"] ?: ""
    val s0608: String get() = v["s0608"] ?: ""
    val s0609: String get() = v["s0609"] ?: ""
    val s0610: String get() = v["s0610"] ?: ""
    val s0611: String get() = v["s0611"] ?: ""
    val s0612: String get() = v["s0612"] ?: ""
    val s0613: String get() = v["s0613"] ?: ""
    val s0614: String get() = v["s0614"] ?: ""
    val s0615: String get() = v["s0615"] ?: ""
    val s0616: String get() = v["s0616"] ?: ""
    val s0617: String get() = v["s0617"] ?: ""
    val s0618: String get() = v["s0618"] ?: ""
    val s0619: String get() = v["s0619"] ?: ""
    val s0620: String get() = v["s0620"] ?: ""
    val s0621: String get() = v["s0621"] ?: ""
    val s0622: String get() = v["s0622"] ?: ""
    val s0623: String get() = v["s0623"] ?: ""
    val s0624: String get() = v["s0624"] ?: ""
    val s0625: String get() = v["s0625"] ?: ""
    val s0626: String get() = v["s0626"] ?: ""
    val s0627: String get() = v["s0627"] ?: ""
    val s0628: String get() = v["s0628"] ?: ""
    val s0629: String get() = v["s0629"] ?: ""
    val s0630: String get() = v["s0630"] ?: ""
    val s0631: String get() = v["s0631"] ?: ""
    val s0632: String get() = v["s0632"] ?: ""
    val s0633: String get() = v["s0633"] ?: ""
    val s0634: String get() = v["s0634"] ?: ""
    val s0635: String get() = v["s0635"] ?: ""
    val s0636: String get() = v["s0636"] ?: ""
    val s0637: String get() = v["s0637"] ?: ""
    val s0638: String get() = v["s0638"] ?: ""
    val s0639: String get() = v["s0639"] ?: ""
    val s0640: String get() = v["s0640"] ?: ""
    val s0641: String get() = v["s0641"] ?: ""
    val s0642: String get() = v["s0642"] ?: ""
    val s0643: String get() = v["s0643"] ?: ""
    val s0644: String get() = v["s0644"] ?: ""
    val s0645: String get() = v["s0645"] ?: ""
    val s0646: String get() = v["s0646"] ?: ""
    val s0647: String get() = v["s0647"] ?: ""
    val s0648: String get() = v["s0648"] ?: ""
    val s0649: String get() = v["s0649"] ?: ""
    val s0650: String get() = v["s0650"] ?: ""
    val s0651: String get() = v["s0651"] ?: ""
    val s0652: String get() = v["s0652"] ?: ""
    val s0653: String get() = v["s0653"] ?: ""
    val s0654: String get() = v["s0654"] ?: ""
    val s0655: String get() = v["s0655"] ?: ""
    val s0656: String get() = v["s0656"] ?: ""
    val s0657: String get() = v["s0657"] ?: ""
    val s0658: String get() = v["s0658"] ?: ""
    val s0659: String get() = v["s0659"] ?: ""
    val s0660: String get() = v["s0660"] ?: ""
    val s0661: String get() = v["s0661"] ?: ""
    val s0662: String get() = v["s0662"] ?: ""
    val s0663: String get() = v["s0663"] ?: ""
    val s0664: String get() = v["s0664"] ?: ""
    val s0665: String get() = v["s0665"] ?: ""
    val s0666: String get() = v["s0666"] ?: ""
    val s0667: String get() = v["s0667"] ?: ""
    val s0668: String get() = v["s0668"] ?: ""
    val s0669: String get() = v["s0669"] ?: ""
    val s0670: String get() = v["s0670"] ?: ""
    val s0671: String get() = v["s0671"] ?: ""
    val s0672: String get() = v["s0672"] ?: ""
    val s0673: String get() = v["s0673"] ?: ""
    val s0674: String get() = v["s0674"] ?: ""
    val s0675: String get() = v["s0675"] ?: ""
    val s0676: String get() = v["s0676"] ?: ""
    val s0677: String get() = v["s0677"] ?: ""
    val s0678: String get() = v["s0678"] ?: ""
    val s0679: String get() = v["s0679"] ?: ""
    val s0680: String get() = v["s0680"] ?: ""
    val s0681: String get() = v["s0681"] ?: ""
    val s0682: String get() = v["s0682"] ?: ""
    val s0683: String get() = v["s0683"] ?: ""
    val s0684: String get() = v["s0684"] ?: ""
    val s0685: String get() = v["s0685"] ?: ""
    val s0686: String get() = v["s0686"] ?: ""
    val s0687: String get() = v["s0687"] ?: ""
    val s0688: String get() = v["s0688"] ?: ""
    val s0689: String get() = v["s0689"] ?: ""
    val s0690: String get() = v["s0690"] ?: ""
    val s0691: String get() = v["s0691"] ?: ""
    val s0692: String get() = v["s0692"] ?: ""
    val s0693: String get() = v["s0693"] ?: ""
    val s0694: String get() = v["s0694"] ?: ""
    val s0695: String get() = v["s0695"] ?: ""
    val s0696: String get() = v["s0696"] ?: ""
    val s0697: String get() = v["s0697"] ?: ""
    val s0698: String get() = v["s0698"] ?: ""
    val s0699: String get() = v["s0699"] ?: ""
    val s0700: String get() = v["s0700"] ?: ""
    val s0701: String get() = v["s0701"] ?: ""
    val s0702: String get() = v["s0702"] ?: ""
    val s0703: String get() = v["s0703"] ?: ""
    val s0704: String get() = v["s0704"] ?: ""
    val s0705: String get() = v["s0705"] ?: ""
    val s0706: String get() = v["s0706"] ?: ""
    val s0707: String get() = v["s0707"] ?: ""
    val s0708: String get() = v["s0708"] ?: ""
    val s0709: String get() = v["s0709"] ?: ""
    val s0710: String get() = v["s0710"] ?: ""
    val s0711: String get() = v["s0711"] ?: ""
    val s0712: String get() = v["s0712"] ?: ""
    val s0713: String get() = v["s0713"] ?: ""
    val s0714: String get() = v["s0714"] ?: ""
    val s0715: String get() = v["s0715"] ?: ""
    val s0716: String get() = v["s0716"] ?: ""
    val s0717: String get() = v["s0717"] ?: ""
    val s0718: String get() = v["s0718"] ?: ""
    val s0719: String get() = v["s0719"] ?: ""
    val s0720: String get() = v["s0720"] ?: ""
    val s0721: String get() = v["s0721"] ?: ""
    val s0722: String get() = v["s0722"] ?: ""
    val s0723: String get() = v["s0723"] ?: ""
    val s0724: String get() = v["s0724"] ?: ""
    val s0725: String get() = v["s0725"] ?: ""
    val s0726: String get() = v["s0726"] ?: ""
    val s0727: String get() = v["s0727"] ?: ""
    val s0728: String get() = v["s0728"] ?: ""
    val s0729: String get() = v["s0729"] ?: ""
    val s0730: String get() = v["s0730"] ?: ""
    val s0731: String get() = v["s0731"] ?: ""
    val s0732: String get() = v["s0732"] ?: ""
    val s0733: String get() = v["s0733"] ?: ""
    val s0734: String get() = v["s0734"] ?: ""
    val s0735: String get() = v["s0735"] ?: ""
    val s0736: String get() = v["s0736"] ?: ""
    val s0737: String get() = v["s0737"] ?: ""
    val s0738: String get() = v["s0738"] ?: ""
    val s0739: String get() = v["s0739"] ?: ""
    val s0740: String get() = v["s0740"] ?: ""
    val s0741: String get() = v["s0741"] ?: ""
    val s0742: String get() = v["s0742"] ?: ""
    val s0743: String get() = v["s0743"] ?: ""
    val s0744: String get() = v["s0744"] ?: ""
    val s0745: String get() = v["s0745"] ?: ""
    val s0746: String get() = v["s0746"] ?: ""
    val s0747: String get() = v["s0747"] ?: ""
    val s0748: String get() = v["s0748"] ?: ""
    val s0749: String get() = v["s0749"] ?: ""
    val s0750: String get() = v["s0750"] ?: ""
    val s0751: String get() = v["s0751"] ?: ""
    val s0752: String get() = v["s0752"] ?: ""
    val s0753: String get() = v["s0753"] ?: ""
    val s0754: String get() = v["s0754"] ?: ""
    val s0755: String get() = v["s0755"] ?: ""
    val s0756: String get() = v["s0756"] ?: ""
    val s0757: String get() = v["s0757"] ?: ""
    val s0758: String get() = v["s0758"] ?: ""
    val s0759: String get() = v["s0759"] ?: ""
    val s0760: String get() = v["s0760"] ?: ""
    val s0761: String get() = v["s0761"] ?: ""
    val s0762: String get() = v["s0762"] ?: ""
    val s0763: String get() = v["s0763"] ?: ""
    val s0764: String get() = v["s0764"] ?: ""
    val s0765: String get() = v["s0765"] ?: ""
    val s0766: String get() = v["s0766"] ?: ""
    val tabChat: String get() = v["tabChat"] ?: ""
    val tabFinance: String get() = v["tabFinance"] ?: ""
    val tabHome: String get() = v["tabHome"] ?: ""
    val tabProfile: String get() = v["tabProfile"] ?: ""
    val tabTrips: String get() = v["tabTrips"] ?: ""
    val translation: String get() = v["translation"] ?: ""
}

val ZhStrings = AppStrings(mapOf(
    "back" to "返回",
    "cancel" to "取消",
    "chat" to "聊天",
    "chatRecords" to "聊天记录管理",
    "clear" to "清空",
    "clearChats" to "清空全部聊天记录",
    "clearedText" to "聊天记录已清空。",
    "clearedTitle" to "已清空",
    "confirm" to "确定",
    "display" to "界面与显示",
    "done" to "完成",
    "exportChats" to "导出全部聊天记录",
    "exportedText" to "聊天记录已保存到手机下载目录。",
    "exportedTitle" to "导出完成",
    "failed" to "失败",
    "fontSize" to "字体大小",
    "language" to "多语言",
    "notifications" to "通知",
    "s0002" to "提示",
    "s0004" to "英文 / 数字 / 标点",
    "s0005" to "邀请码",
    "s0006" to " 天",
    "s0007" to "加载中…",
    "s0008" to "已取消",
    "s0009" to "已完成",
    "s0010" to "状态",
    "s0011" to "拍照",
    "s0012" to "文件",
    "s0013" to "未知错误",
    "s0014" to "车辆信息",
    "s0015" to "转账",
    "s0016" to "仅数字",
    "s0017" to "仅英文",
    "s0018" to "从相册选择",
    "s0019" to "代收SGD",
    "s0020" to "位置",
    "s0021" to "保存成功",
    "s0022" to "全部",
    "s0023" to "关于我们",
    "s0024" to "删除",
    "s0025" to "复制",
    "s0026" to "头像",
    "s0027" to "官方通知",
    "s0028" to "客服小星",
    "s0029" to "已代收",
    "s0030" to "已入账",
    "s0031" to "已通过",
    "s0032" to "我的邀请码",
    "s0033" to "报销",
    "s0034" to "拍照或从相册选择",
    "s0035" to "提交成功",
    "s0036" to "未命名",
    "s0037" to "用户",
    "s0038" to "薪资",
    "s0039" to "请先登录",
    "s0040" to "调度 · 陈师傅",
    "s0041" to "选择日期",
    "s0042" to "金额",
    "s0044" to " 单",
    "s0045" to "个人信息",
    "s0046" to "代收MYR",
    "s0047" to "关于平台",
    "s0048" to "切换密码可见",
    "s0049" to "名片",
    "s0050" to "城市",
    "s0051" to "姓名",
    "s0052" to "密码管理",
    "s0053" to "州属",
    "s0054" to "开销资金",
    "s0055" to "待完成",
    "s0056" to "性别",
    "s0057" to "我的值班",
    "s0058" to "我的评价",
    "s0059" to "我的钱包",
    "s0060" to "手机号",
    "s0061" to "拍照或上传文件",
    "s0062" to "提交中…",
    "s0063" to "提交失败",
    "s0064" to "搜索",
    "s0065" to "星域臻旅 · 报销",
    "s0066" to "昵称",
    "s0067" to "更多安全设置",
    "s0068" to "生日日期",
    "s0069" to "登录设备管理",
    "s0070" to "相册",
    "s0071" to "确认",
    "s0072" to "确认密码",
    "s0073" to "花园",
    "s0076" to "货币",
    "s0077" to "进行中",
    "s0078" to "退出",
    "s0079" to "邮箱地址",
    "s0080" to "邮编",
    "s0081" to "门牌及街道",
    "s0082" to "隐私信息",
    "s0083" to " / 7 档",
    "s0084" to "KYC认证",
    "s0085" to "XY-0001 · 星域臻旅",
    "s0086" to "XY-S-0102 · 星域臻旅 · 客服中心",
    "s0087" to "XY-S-0231 · 星域臻旅 · 调度中心",
    "s0088" to "[位置] ",
    "s0089" to "[名片] ",
    "s0090" to "[文件] ",
    "s0091" to "[语音] ",
    "s0092" to "《用户服务协议》",
    "s0093" to "《隐私政策》",
    "s0094" to "上传马来西亚身份证",
    "s0095" to "与",
    "s0096" to "两次输入的密码不一致",
    "s0097" to "个人",
    "s0098" to "中文 / 英文 / 数字 / 标点",
    "s0099" to "二维码还没准备好",
    "s0100" to "仅支持拍照",
    "s0101" to "仅英文和空格",
    "s0102" to "今日单数",
    "s0103" to "今日报销",
    "s0104" to "今日薪资",
    "s0105" to "从",
    "s0106" to "休息日期",
    "s0107" to "保存中…",
    "s0108" to "保存失败",
    "s0109" to "保存失败，请检查存储权限后重试",
    "s0110" to "保存更改",
    "s0111" to "值班详情",
    "s0112" to "全部标记已读",
    "s0113" to "公司",
    "s0114" to "关闭闪光灯",
    "s0115" to "再次输入新密码",
    "s0116" to "分享",
    "s0117" to "加油",
    "s0118" to "原因",
    "s0119" to "品牌",
    "s0120" to "图片",
    "s0121" to "型号",
    "s0122" to "增加车辆",
    "s0123" to "备注",
    "s0124" to "审核中",
    "s0125" to "对",
    "s0126" to "将二维码放入框内",
    "s0127" to "将文字翻译为",
    "s0128" to "居住地",
    "s0129" to "已报销",
    "s0130" to "平均评分",
    "s0131" to "待通过",
    "s0132" to "微信",
    "s0133" to "微信号",
    "s0134" to "忘记密码？",
    "s0135" to "我已阅读并同意",
    "s0136" to "扫描结果",
    "s0137" to "扫码",
    "s0138" to "提交报销",
    "s0139" to "放假",
    "s0140" to "新密码",
    "s0141" to "无需收钱",
    "s0142" to "日期",
    "s0143" to "日期：",
    "s0144" to "星域臻旅 · 公司名片",
    "s0145" to "星域臻旅 · 薪资结算",
    "s0146" to "是",
    "s0147" to "更多",
    "s0148" to "更改成功",
    "s0149" to "更新",
    "s0150" to "有必填字段没填",
    "s0151" to "有效",
    "s0152" to "未完成",
    "s0153" to "查看申请",
    "s0154" to "注 册",
    "s0155" to "注册",
    "s0156" to "注册资料写入失败：",
    "s0157" to "洗车",
    "s0158" to "流水",
    "s0159" to "流水记录",
    "s0160" to "浅色",
    "s0161" to "深色",
    "s0162" to "清空聊天记录",
    "s0163" to "版本更新",
    "s0164" to "生成时间：",
    "s0165" to "申请上交",
    "s0166" to "申请休息",
    "s0167" to "申请休息天数",
    "s0168" to "申请类型",
    "s0169" to "登录",
    "s0170" to "目的地",
    "s0171" to "类型",
    "s0172" to "结束日期",
    "s0173" to "给",
    "s0174" to "聊天详情",
    "s0175" to "英文 / 数字 / 符号，至少 8 位",
    "s0176" to "设置新密码失败",
    "s0177" to "语音通话通知",
    "s0178" to "请假",
    "s0179" to "请输入金额",
    "s0180" to "请选择",
    "s0181" to "起点",
    "s0182" to "跟随系统",
    "s0183" to "车款",
    "s0184" to "转账金额",
    "s0185" to "输入特定金额",
    "s0186" to "退出登录",
    "s0187" to "退出该设备",
    "s0188" to "选填 · 英文 / 数字 / 标点",
    "s0189" to "选择文档",
    "s0190" to "邮箱",
    "s0191" to "重置邮件发送失败",
    "s0192" to "钱包余额",
    "s0193" to "链接已复制",
    "s0194" to "闪光灯",
    "s0195" to " *必填",
    "s0196" to " 个",
    "s0197" to " 单 · 薪资 ",
    "s0198" to " 天 · ",
    "s0199" to " 条通知吗？",
    "s0200" to " 秒",
    "s0201" to " 项",
    "s0202" to "%s 已上传",
    "s0203" to "1  从",
    "s0204" to "2  给",
    "s0205" to "3  转账金额",
    "s0206" to "4  其他详情",
    "s0207" to "5 单合并结算",
    "s0208" to "6 位验证码",
    "s0209" to "6 单合并结算",
    "s0210" to "JXY 8888 · 丰田 Innova",
    "s0211" to "JXY 8888 加油费",
    "s0212" to "Johor Bahru，柔佛",
    "s0213" to "VKL 5566 · 日产 Serena",
    "s0214" to "VKL 5566 洗车费",
    "s0215" to "WMB 2233 · 本田 CR-V",
    "s0216" to "WMB 2233 过路费",
    "s0217" to "[图片 x",
    "s0218" to "[图片与视频] ",
    "s0219" to "\\\"下载星域臻旅客户端并使用我们的平台进行下单",
    "s0220" to "e-statement PDF 已生成并保存到手机（下载/XYZL）",
    "s0221" to "— 尊荣邀制 · 至尊之旅 —",
    "s0222" to "⚠️ 还有必填项没填：",
    "s0223" to "《KYC实名认证合规条款》",
    "s0224" to "《终端用户软件许可》",
    "s0225" to "《终端用户软件许可协议》",
    "s0226" to "」的聊天记录吗？",
    "s0227" to "上交",
    "s0228" to "上传失败",
    "s0229" to "上传失败：",
    "s0230" to "上传护照",
    "s0231" to "上传收据证明",
    "s0232" to "上传证件",
    "s0233" to "上传马来西亚护照",
    "s0234" to "上传马来西亚驾照",
    "s0235" to "上传驾照",
    "s0236" to "下载二维码",
    "s0237" to "业务",
    "s0238" to "业务地点",
    "s0239" to "两次密码不一致",
    "s0240" to "严格按照马来西亚相关法规处理用户、司机个人资料",
    "s0241" to "个人信息已更新",
    "s0242" to "个人原因",
    "s0243" to "个人详情",
    "s0244" to "中国网站",
    "s0245" to "乘客 · 张先生",
    "s0246" to "乘客 · 李女士",
    "s0247" to "乘客 · 王先生",
    "s0248" to "乘客 · 陈女士",
    "s0249" to "了解更多",
    "s0250" to "二维码",
    "s0251" to "二维码已保存到手机相册",
    "s0252" to "交易流水记录",
    "s0253" to "交易笔数：",
    "s0254" to "交易编号",
    "s0255" to "交易编号：",
    "s0256" to "交易编号：%s\\n",
    "s0257" to "交易详情",
    "s0258" to "人数",
    "s0259" to "仅拍照上传",
    "s0260" to "今日代收MYR",
    "s0261" to "今日代收MYR：",
    "s0262" to "今日代收SGD",
    "s0263" to "今日代收SGD：",
    "s0264" to "今日单数：",
    "s0265" to "今日报销：",
    "s0266" to "今日薪资 · 6 单",
    "s0267" to "今日薪资：",
    "s0268" to "付款日期",
    "s0269" to "代收",
    "s0270" to "代收 ",
    "s0271" to "代收 MYR",
    "s0272" to "代收 SGD",
    "s0273" to "代收新币",
    "s0274" to "代收账户",
    "s0275" to "代收车费 · 张先生",
    "s0276" to "代收车费 · 李女士",
    "s0277" to "代收车费 · 王先生",
    "s0278" to "代收车费 · 陈女士",
    "s0279" to "代收马币",
    "s0280" to "休息",
    "s0281" to "休息 ",
    "s0282" to "休息中",
    "s0283" to "住宿",
    "s0284" to "你们已经是好友",
    "s0285" to "使用听筒播放消息",
    "s0286" to "使用独立的发送按钮",
    "s0287" to "例如 WXY 1234",
    "s0288" to "依托新山独特的地理位置，我们聚焦马来西亚 ↔ 新加坡跨境接送、定点包车、预约接送业务。平台打通乘客下单、智能派单、司机调度、线上核验、行程跟踪与线上支付完整链路。",
    "s0289" to "保存失败，稍后重试",
    "s0290" to "保存失败，请检查相册权限后重试",
    "s0291" to "保存失败：",
    "s0292" to "保留查看过的原图、原视频",
    "s0293" to "保险证明",
    "s0294" to "修改",
    "s0295" to "值班记录",
    "s0296" to "值班详情 PDF 已生成并保存到手机（下载/XYZL）",
    "s0297" to "值班详情_",
    "s0298" to "值班车辆",
    "s0299" to "停止接单",
    "s0300" to "全选",
    "s0301" to "公司主体",
    "s0302" to "公司统一放假",
    "s0303" to "共 128 条评价",
    "s0304" to "关闭",
    "s0305" to "其他",
    "s0306" to "其他登录方式",
    "s0307" to "其他详情",
    "s0308" to "分享个人二维码",
    "s0309" to "分享你的二维码后，他人可以将你添加为联系人。分享你的链接后，他人可以开始与你聊天。",
    "s0310" to "创建密码",
    "s0311" to "删除后这条记录不再显示。",
    "s0312" to "删除失败，稍后重试",
    "s0313" to "删除设备记录",
    "s0314" to "删除通知",
    "s0315" to "到期日期",
    "s0316" to "到期日期必填（格式 YYYY-MM-DD，点上方选择）。请上传清晰的证件正面照，四角完整、文字可读；提交后状态变为「待审核」。",
    "s0317" to "剩余休息天数",
    "s0318" to "剩余天数",
    "s0319" to "加好友",
    "s0320" to "加油 · JXY 8888",
    "s0321" to "勾选协议",
    "s0322" to "包括「图片、视频和文件」。",
    "s0323" to "原密码",
    "s0324" to "原密码不正确",
    "s0325" to "发消息…",
    "s0326" to "发现新版本 %s，请更新后继续使用。",
    "s0327" to "发现新版本 v%s",
    "s0328" to "发送",
    "s0329" to "发送名片",
    "s0330" to "发送消息",
    "s0331" to "发送重置邮件",
    "s0332" to "取消全选",
    "s0333" to "可用余额（%s）",
    "s0334" to "司机很准时，车也干净，全程很稳，满意！",
    "s0335" to "司机端内消息横幅",
    "s0336" to "司机端打开时",
    "s0337" to "司机端未打开时",
    "s0338" to "合计：%s",
    "s0339" to "吉隆坡 → 槟城 · 送机",
    "s0340" to "吉隆坡机场 → 新加坡 行程代收",
    "s0341" to "吉隆坡机场 → 新山 行程代收",
    "s0342" to "否",
    "s0343" to "听筒",
    "s0344" to "品牌列表是空的（se_vehicle_brands 没有 is_active = true 的数据），先让管理端加品牌。",
    "s0345" to "品牌和型号都要选",
    "s0346" to "国际网站",
    "s0347" to "图片与视频",
    "s0348" to "在聊天、网页及图片中使用翻译功能时，文字会被翻译为所选语言。",
    "s0349" to "处理中",
    "s0350" to "备注：",
    "s0351" to "备注：%s\\n",
    "s0352" to "复制链接",
    "s0353" to "大人",
    "s0354" to "女",
    "s0355" to "好友申请已发送",
    "s0356" to "好评率 96%",
    "s0357" to "完成单数",
    "s0358" to "完成提交",
    "s0359" to "官方",
    "s0360" to "家中有事，需请两天假",
    "s0361" to "密码已更新，下次登录用新密码。",
    "s0362" to "密码（未达标）",
    "s0363" to "对方已接收",
    "s0364" to "对方待接收",
    "s0365" to "对方还没有钱包账号，无法转账",
    "s0366" to "对话",
    "s0367" to "导出失败，请稍后重试。",
    "s0368" to "导航到",
    "s0369" to "小孩",
    "s0370" to "尝试太频繁，请稍后再试",
    "s0371" to "已上传",
    "s0372" to "已上传收据",
    "s0373" to "已休息天数",
    "s0374" to "已发送过好友申请，等待对方确认",
    "s0375" to "已复制",
    "s0376" to "已存在相同记录",
    "s0377" to "已拒绝",
    "s0378" to "已授权车辆",
    "s0379" to "已提交",
    "s0380" to "已是最新版本",
    "s0381" to "已登录",
    "s0382" to "已登录的设备",
    "s0383" to "已绑定",
    "s0384" to "已读",
    "s0385" to "已跨境天数",
    "s0386" to "已过期 · ",
    "s0387" to "已退出",
    "s0388" to "已退出的设备",
    "s0389" to "已选择",
    "s0390" to "已选择 ",
    "s0391" to "已静音",
    "s0392" to "已驳回",
    "s0393" to "市区包车 4 小时",
    "s0394" to "布城 → 云顶高原",
    "s0395" to "帐号",
    "s0396" to "帐号已复制到剪贴板",
    "s0397" to "帐户号码",
    "s0398" to "帮助与关于",
    "s0399" to "帮助与反馈",
    "s0400" to "帮忙搬行李，态度好，下次还会预约。",
    "s0401" to "平台仅作为出行信息调度与订单撮合工具",
    "s0402" to "平台政策与协议",
    "s0403" to "平台本身不直接拥有、运营任何车辆，车辆以及司机均为经过平台审核的注册合作司机。",
    "s0404" to "开启后，保留「已发送」和「已接收并查看」的原图原视频。开启前的原图原视频不受影响。",
    "s0405" to "开启后，将跟随系统打开或关闭深色模式。",
    "s0406" to "开启后，所有聊天收到的消息会被自动翻译。",
    "s0407" to "开启后，键盘上的发送按钮会被替换成换行。",
    "s0408" to "开始接单",
    "s0409" to "开销日期",
    "s0410" to "归属公司",
    "s0411" to "归属车辆",
    "s0412" to "当前任务",
    "s0413" to "当前位置（定位不可用）",
    "s0414" to "当前余额（%s）",
    "s0415" to "当前版本",
    "s0416" to "当前版本 %s 已是最新版本。",
    "s0417" to "当时接人时间",
    "s0418" to "当时放人时间",
    "s0419" to "当月累计报销",
    "s0420" to "当月累计薪资",
    "s0421" to "待审批",
    "s0422" to "待审核",
    "s0423" to "待通过验证车辆",
    "s0424" to "必须 8-14 个字",
    "s0425" to "必须包含 1 个大写字母",
    "s0426" to "必须包含 1 个小写字母",
    "s0427" to "必须包含 1 个数字",
    "s0428" to "必须包含 1 个特殊标点符号",
    "s0429" to "忘记密码",
    "s0430" to "我们坚持本地合规运营：",
    "s0431" to "我方已发送",
    "s0432" to "我方待发送",
    "s0433" to "我无法获取该车主身份证",
    "s0434" to "我的",
    "s0435" to "我的二维码",
    "s0436" to "我的信息",
    "s0437" to "我的当前位置",
    "s0438" to "我的车辆",
    "s0439" to "所属公司",
    "s0440" to "所属公司：",
    "s0441" to "所有平台入驻司机必须完成身份资料提交以及 KYC 实名认证",
    "s0442" to "手动选择",
    "s0443" to "打印",
    "s0444" to "打印 PDF",
    "s0445" to "打印 e-statement",
    "s0446" to "打开",
    "s0447" to "扫描二维码",
    "s0448" to "扫码添加为好友",
    "s0449" to "扬声器",
    "s0450" to "找不到你的个人档案（se_personal_profiles 里没有你的记录）",
    "s0451" to "找不到你的司机档案（se_driver_profiles 里没有你的记录）",
    "s0452" to "找不到你的司机档案，证件没法登记",
    "s0453" to "找不到你的账户（se_account_members 没有你的记录）",
    "s0454" to "找不到司机档案，证件照没登记",
    "s0455" to "找不到这单",
    "s0456" to "把所有通知标成已读",
    "s0457" to "护照",
    "s0458" to "护照照片",
    "s0459" to "报销类型",
    "s0460" to "报销类型：\${entry.expenseType ?: ",
    "s0461" to "拍照 / 相册 / 文档",
    "s0462" to "拍照或从相册选择证件照片",
    "s0463" to "拖动下面的滑块，可设置字体大小",
    "s0464" to "拖动下面的滑块，可设置字体大小。设置后，会改变聊天和应用全部的字体大小。如果在使用过程中存在问题或意见，可反馈给官方团队。",
    "s0465" to "挂断",
    "s0466" to "按住 说话",
    "s0467" to "振动",
    "s0468" to "接单中",
    "s0469" to "接单时间",
    "s0470" to "提交失败，稍后重试",
    "s0471" to "提交失败：",
    "s0472" to "提现",
    "s0473" to "撤销申请",
    "s0474" to "播放",
    "s0475" to "支付与收款",
    "s0476" to "支持拍照 / 相册",
    "s0477" to "收据",
    "s0478" to "收据证明",
    "s0479" to "收款",
    "s0480" to "收金额",
    "s0481" to "整体不错，会再介绍给朋友。",
    "s0482" to "新加坡 → 新山 行程代收",
    "s0483" to "新密码不能和原密码一样",
    "s0484" to "新山 → 吉隆坡机场 行程代收",
    "s0485" to "新转账",
    "s0486" to "无法保存",
    "s0487" to "无法添加对方为好友",
    "s0488" to "日期 时间",
    "s0489" to "日期时间",
    "s0490" to "日期时间：",
    "s0491" to "日期时间：%s\\n",
    "s0492" to "星域",
    "s0493" to "星域司机",
    "s0494" to "星域臻旅 Stellar Elite Travel & Transport",
    "s0495" to "星域臻旅 · 交易收据\\n",
    "s0496" to "星域臻旅 司机端",
    "s0497" to "星域臻旅 司机端 · e-Statement（电子对账单）",
    "s0498" to "星域臻旅 司机端 · 值班详情",
    "s0499" to "星域臻旅 我的二维码",
    "s0500" to "星域臻旅为星域集团旗下业务板块；集团同时覆盖餐饮、科技开发、数字化系统研发等多个业务，旗下科技团队自研整套平台、司机端、乘客端与后台管理系统。",
    "s0501" to "星域臻旅版权所有",
    "s0502" to "星域臻旅车服有限公司",
    "s0503" to "星域臻旅（Stellar Elite Travel）隶属于 STELLAR ELITE ENTERPRISE，企业注册于马来西亚柔佛，是面向新马两地的跨境出行服务平台。",
    "s0504" to "昨日薪资 · 5 单",
    "s0505" to "是否车主",
    "s0506" to "暂无已登录设备",
    "s0507" to "暂无已退出设备",
    "s0508" to "暂无已验证车辆",
    "s0509" to "暂无待验证车辆",
    "s0510" to "暂无消息",
    "s0511" to "暂无联系人，点「新转账」按账号搜索",
    "s0512" to "暂无行程",
    "s0513" to "暂无记录",
    "s0514" to "暂无评价",
    "s0515" to "暂无进行中的任务",
    "s0516" to "暂无通知",
    "s0517" to "暂无邀请码",
    "s0518" to "暂无钱包账户",
    "s0519" to "更换",
    "s0520" to "更换值班车辆",
    "s0521" to "更改中…",
    "s0522" to "更改失败",
    "s0523" to "更新 KYC 认证",
    "s0524" to "更新功能不可用",
    "s0525" to "最后登录时间",
    "s0526" to "最新版本",
    "s0527" to "最近评价我的记录",
    "s0528" to "最近评价该司机的记录",
    "s0529" to "有效 · 至 ",
    "s0530" to "未上传",
    "s0531" to "未填微信",
    "s0532" to "未填电话",
    "s0533" to "未找到该帐户",
    "s0534" to "未检测",
    "s0535" to "未知设备",
    "s0536" to "未绑定",
    "s0537" to "未设置值班车辆",
    "s0538" to "未识别到二维码",
    "s0539" to "未读",
    "s0540" to "机场接机",
    "s0541" to "李小姐",
    "s0542" to "来自",
    "s0543" to "来自：",
    "s0544" to "来自：%s\\n",
    "s0545" to "松开 发送 · ",
    "s0546" to "查找聊天内容",
    "s0547" to "标记已读",
    "s0548" to "检测中…",
    "s0549" to "检测失败",
    "s0550" to "检测版本",
    "s0551" to "正在下载新版本，请稍候…",
    "s0552" to "正在接收新订单",
    "s0553" to "正在更新 v%s",
    "s0554" to "正在识别…",
    "s0555" to "正在读取绑定状态…",
    "s0556" to "没有可用车辆",
    "s0557" to "没有帐号？",
    "s0558" to "没有找到相关聊天记录",
    "s0559" to "没有权限提交，先看下数据库策略",
    "s0560" to "没有权限改这条数据",
    "s0561" to "没有权限退出这台设备",
    "s0562" to "没有识别到二维码，换一张试试",
    "s0563" to "没有麦克风权限，无法录音",
    "s0564" to "注册地址",
    "s0565" to "注册失败，请重试",
    "s0566" to "注册失败：拿不到登录状态，请重试",
    "s0567" to "注册成功",
    "s0568" to "注册编号",
    "s0569" to "泰米尔文",
    "s0570" to "洗车 · VKL 5566",
    "s0571" to "消息免打扰",
    "s0572" to "消息提示音",
    "s0573" to "深色 / 浅色模式",
    "s0574" to "深色模式",
    "s0575" to "清掉全部通知（软删除，可恢复）",
    "s0576" to "清空只对你自己隐藏（user_cleared_at），服务器上的聊天记录不会被删。",
    "s0577" to "清空后本机全部聊天记录将被删除且无法恢复。",
    "s0578" to "清空当前聊天记录",
    "s0579" to "清空或导出会作用于本机保存的全部聊天记录。",
    "s0580" to "清空通知",
    "s0581" to "滑动确认接人",
    "s0582" to "滑动确认放人",
    "s0583" to "点击「日期」选择一天，或再点一次重新选",
    "s0584" to "点击以下下载链接\\\"",
    "s0585" to "点击右侧按钮开始接单",
    "s0586" to "点击查看（未开启定位）",
    "s0587" to "点击设置头像",
    "s0588" to "状态：",
    "s0589" to "状态：%s\\n",
    "s0590" to "用",
    "s0591" to "申请",
    "s0592" to "申请 ",
    "s0593" to "申请「%s」已提交。",
    "s0594" to "申请「上交%s」%s 已提交。",
    "s0595" to "申请「开销资金」%s %s 已提交。",
    "s0596" to "申请信息",
    "s0597" to "申请发薪",
    "s0598" to "申请日期",
    "s0599" to "申请时间",
    "s0600" to "申请详情",
    "s0601" to "男",
    "s0602" to "登 录",
    "s0603" to "登录后才能保存个人信息",
    "s0604" to "登录后才能提交车辆",
    "s0605" to "登录后才能更新 KYC 认证",
    "s0606" to "登录状态刷新失败，请重新登录",
    "s0607" to "知道了",
    "s0608" to "确定删除选中的 ",
    "s0609" to "确定清空与「",
    "s0610" to "确定清空全部通知吗？",
    "s0611" to "确定要退出当前账号吗？",
    "s0612" to "确认密码与创建密码不符合",
    "s0613" to "确认提交",
    "s0614" to "确认更改",
    "s0615" to "确认更新",
    "s0616" to "确认申请",
    "s0617" to "确认转账",
    "s0618" to "稍后",
    "s0619" to "稍后重试",
    "s0620" to "立即更新",
    "s0621" to "第三方绑定即将开放",
    "s0622" to "类型：",
    "s0623" to "类型：%s\\n",
    "s0624" to "系统消息通知",
    "s0625" to "系统版本",
    "s0626" to "绑定失败：",
    "s0627" to "绑定状态读 se_user_oauth_bindings；第三方登录要接 Apple / Facebook / WeChat 的 SDK，接入后再走授权流程。",
    "s0628" to "继续",
    "s0629" to "维修",
    "s0630" to "编辑",
    "s0631" to "网络异常或暂不支持检测，请稍后重试。",
    "s0632" to "网络异常，请检查网络后重试",
    "s0633" to "置顶",
    "s0634" to "置顶聊天",
    "s0635" to "翻译为 ",
    "s0636" to "联系人名片",
    "s0637" to "联系我们",
    "s0638" to "联系方式",
    "s0639" to "自动下载在其他设备查看过的内容",
    "s0640" to "自动生成，可自定义，唯一",
    "s0641" to "自动翻译聊天中收到的消息",
    "s0642" to "行李",
    "s0643" to "行程已完成",
    "s0644" to "行程薪资",
    "s0645" to "行程详情",
    "s0646" to "视频画面",
    "s0647" to "视频通话",
    "s0648" to "视频通话即将开放",
    "s0649" to "订单编号",
    "s0650" to "订单编号：",
    "s0651" to "订单编号：%s\\n",
    "s0652" to "设备信息写入失败：",
    "s0653" to "设备名称",
    "s0654" to "设备类型",
    "s0655" to "设备详情",
    "s0656" to "设置后，会改变聊天和应用全部的字体大小。如果在使用过程中存在问题或意见，可反馈给官方团队",
    "s0657" to "设置头像",
    "s0658" to "证件已提交，等待审核",
    "s0659" to "证件照登记失败：",
    "s0660" to "评价记录",
    "s0661" to "该二维码有效时间 %s 秒",
    "s0662" to "该邮箱已注册，但密码对不上。如果你就是本人，请直接登录；忘记密码可以点「忘记密码」。",
    "s0663" to "语音",
    "s0664" to "语音通话",
    "s0665" to "语音通话即将开放",
    "s0666" to "请先拍照或从相册选一张证件照片",
    "s0667" to "请再次输入密码",
    "s0668" to "请向所属公司索取授权车辆",
    "s0669" to "请更新至最新版本",
    "s0670" to "请输入 8-14 位密码",
    "s0671" to "请输入原因",
    "s0672" to "请输入原密码",
    "s0673" to "请输入备注（选填）",
    "s0674" to "请输入密码",
    "s0675" to "请输入对方帐户号码",
    "s0676" to "请输入手机号 / 邮箱",
    "s0677" to "请输入注册时使用的邮箱地址，我们会把重置密码的链接发到该邮箱。",
    "s0678" to "请选择类型",
    "s0679" to "请选择证件的到期日期（YYYY-MM-DD）",
    "s0680" to "请选择车辆",
    "s0681" to "请重新输入原密码，或点「忘记密码？」用邮箱重置。",
    "s0682" to "读不到你选的文件，换一张试试",
    "s0683" to "账号与密码错误",
    "s0684" to "账号已创建，请用刚才的邮箱和密码登录。",
    "s0685" to "账号管理",
    "s0686" to "资料没填全",
    "s0687" to "超时",
    "s0688" to "跨境",
    "s0689" to "跨境包车、出行预约、线上出行平台运营",
    "s0690" to "路税证明",
    "s0691" to "路线熟悉，沟通也顺畅，等待时间稍微久了一点。",
    "s0692" to "身份证",
    "s0693" to "身份证照片",
    "s0694" to "车主马来西亚身份证",
    "s0695" to "车前排照片",
    "s0696" to "车前照片",
    "s0697" to "车卡证明",
    "s0698" to "车后排照片",
    "s0699" to "车后照片",
    "s0700" to "车牌",
    "s0701" to "车牌号",
    "s0702" to "车辆",
    "s0703" to "车辆已登记，但证件登记失败：",
    "s0704" to "车辆照片",
    "s0705" to "车辆登记失败：没拿到车辆记录号",
    "s0706" to "车辆证明",
    "s0707" to "车辆资料已提交，等待审核",
    "s0708" to "转账信息不完整，请返回检查",
    "s0709" to "转账失败",
    "s0710" to "转账提交失败",
    "s0711" to "转账日期",
    "s0712" to "返回登录",
    "s0713" to "还没填到期日期",
    "s0714" to "还没有官方通知",
    "s0715" to "还没有聊天会话",
    "s0716" to "还没选文件",
    "s0717" to "这不是星域臻旅的二维码",
    "s0718" to "这个 ID 已被别人用了，换一个吧",
    "s0719" to "这台车（同一个车牌）已经登记过了",
    "s0720" to "这张图片里没有找到二维码，换一张再试试。",
    "s0721" to "这是你自己的二维码",
    "s0722" to "迟到",
    "s0723" to "退出后，下次在该设备登录时需要进行安全验证。",
    "s0724" to "退出失败，稍后重试",
    "s0725" to "选填 · 填写后归属该公司，不填为个人",
    "s0726" to "选择品牌",
    "s0727" to "选择型号",
    "s0728" to "选择车款",
    "s0729" to "通用",
    "s0730" to "通知操作",
    "s0731" to "通过 Apple 登录",
    "s0732" to "通过 Facebook 登录",
    "s0733" to "通过 WeChat 登录",
    "s0734" to "通过日期时间",
    "s0735" to "邀请码只有一种：归属某个账号。个人/独资/企业显示自己的；司机/客服/客户显示归属公司的。客户分享给好友，好友填同一码即归入同一公司。",
    "s0736" to "邀请码已复制",
    "s0737" to "邮件",
    "s0738" to "邮箱还没验证，请先到邮箱点验证链接",
    "s0739" to "重发",
    "s0740" to "重发 ",
    "s0741" to "重建密码",
    "s0742" to "重置邮件已发送到 %s。请到邮箱点开里面的链接，回到 App 就能设置新密码。",
    "s0743" to "金额（%s）",
    "s0744" to "金额（%s）：%s\\n",
    "s0745" to "金额：",
    "s0746" to "链接",
    "s0747" to "链接分享方式",
    "s0748" to "链接已复制，正在打开 Instagram",
    "s0749" to "链接已复制，正在打开微信",
    "s0750" to "陈太太",
    "s0751" to "陈师傅",
    "s0752" to "隐私信息已更新",
    "s0753" to "集团背景",
    "s0754" to "静音",
    "s0755" to "预支薪水",
    "s0756" to "预览字体大小",
    "s0757" to "马六甲一日游",
    "s0758" to "马来文",
    "s0759" to "驾照",
    "s0760" to "驾照照片",
    "s0761" to "验证日期时间",
    "s0762" to "验证码",
    "s0763" to "验证码已发送至",
    "s0764" to "，邀请码：\\\"",
    "s0765" to "：未选择文件",
    "s0766" to "：读不到本地文件",
    "tabChat" to "聊天",
    "tabFinance" to "财务",
    "tabHome" to "首页",
    "tabProfile" to "我",
    "tabTrips" to "行程",
    "translation" to "翻译",
))

val EnStrings = AppStrings(mapOf(
    "back" to "Back",
    "cancel" to "Cancel",
    "chat" to "Chat",
    "chatRecords" to "Chat History",
    "clear" to "Clear",
    "clearChats" to "Clear all chats",
    "clearedText" to "Chat history cleared.",
    "clearedTitle" to "Cleared",
    "confirm" to "OK",
    "display" to "Display",
    "done" to "Done",
    "exportChats" to "Export all chats",
    "exportedText" to "Chat history saved to Downloads.",
    "exportedTitle" to "Export complete",
    "failed" to "Failed",
    "fontSize" to "Font Size",
    "language" to "Language",
    "notifications" to "Notifications",
    "s0002" to "Notice",
    "s0004" to "Letters / numbers / punctuation",
    "s0005" to "Invite Code",
    "s0006" to " days",
    "s0007" to "Loading…",
    "s0008" to "Cancelled",
    "s0009" to "Completed",
    "s0010" to "Status",
    "s0011" to "Take Photo",
    "s0012" to "File",
    "s0013" to "Unknown error",
    "s0014" to "Vehicle Info",
    "s0015" to "Transfer",
    "s0016" to "Numbers only",
    "s0017" to "English letters only",
    "s0018" to "Choose from Gallery",
    "s0019" to "SGD Collection",
    "s0020" to "Location",
    "s0021" to "Saved",
    "s0022" to "All",
    "s0023" to "About Us",
    "s0024" to "Delete",
    "s0025" to "Copy",
    "s0026" to "Avatar",
    "s0027" to "Official Notifications",
    "s0028" to "Customer Service",
    "s0029" to "Collected",
    "s0030" to "Credited",
    "s0031" to "Approved",
    "s0032" to "My Invite Code",
    "s0033" to "Reimbursement",
    "s0034" to "Take a photo or choose from gallery",
    "s0035" to "Submitted",
    "s0036" to "Unnamed",
    "s0037" to "User",
    "s0038" to "Salary",
    "s0039" to "Please log in first",
    "s0040" to "Dispatch · Mr. Chen",
    "s0041" to "Select Date",
    "s0042" to "Amount",
    "s0044" to " trips",
    "s0045" to "Personal Info",
    "s0046" to "MYR Collection",
    "s0047" to "About Platform",
    "s0048" to "Toggle password visibility",
    "s0049" to "Contact Card",
    "s0050" to "City",
    "s0051" to "Name",
    "s0052" to "Password Management",
    "s0053" to "State",
    "s0054" to "Expense Funds",
    "s0055" to "To Do",
    "s0056" to "Gender",
    "s0057" to "My Duty",
    "s0058" to "My Reviews",
    "s0059" to "My Wallet",
    "s0060" to "Phone Number",
    "s0061" to "Take a photo or upload a file",
    "s0062" to "Submitting…",
    "s0063" to "Submission failed",
    "s0064" to "Search",
    "s0065" to "Stellar Elite · Reimbursement",
    "s0066" to "Nickname",
    "s0067" to "More Security Settings",
    "s0068" to "Date of Birth",
    "s0069" to "Login Devices",
    "s0070" to "Gallery",
    "s0071" to "Confirm",
    "s0072" to "Confirm Password",
    "s0073" to "Garden",
    "s0076" to "Currency",
    "s0077" to "In Progress",
    "s0078" to "Exit",
    "s0079" to "Email Address",
    "s0080" to "Postcode",
    "s0081" to "Street Address",
    "s0082" to "Privacy Info",
    "s0083" to " / 7",
    "s0084" to "KYC Verification",
    "s0085" to "XY-0001 · Stellar Elite",
    "s0086" to "XY-S-0102 · Stellar Elite · Customer Service",
    "s0087" to "XY-S-0231 · Stellar Elite · Dispatch Center",
    "s0088" to "[Location] ",
    "s0089" to "[Contact] ",
    "s0090" to "[File] ",
    "s0091" to "[Voice] ",
    "s0092" to "User Service Agreement",
    "s0093" to "Privacy Policy",
    "s0094" to "Upload Malaysian IC",
    "s0095" to "and",
    "s0096" to "Passwords do not match",
    "s0097" to "Personal",
    "s0098" to "Chinese / letters / numbers / punctuation",
    "s0099" to "QR code is not ready",
    "s0100" to "Camera only",
    "s0101" to "English letters and spaces only",
    "s0102" to "Today's Trips",
    "s0103" to "Today's Reimbursement",
    "s0104" to "Today's Salary",
    "s0105" to "From",
    "s0106" to "Rest Date",
    "s0107" to "Saving…",
    "s0108" to "Save failed",
    "s0109" to "Save failed. Check storage permission and try again.",
    "s0110" to "Save Changes",
    "s0111" to "Duty Details",
    "s0112" to "Mark All as Read",
    "s0113" to "Company",
    "s0114" to "Turn off flashlight",
    "s0115" to "Re-enter new password",
    "s0116" to "Share",
    "s0117" to "Fuel",
    "s0118" to "Reason",
    "s0119" to "Brand",
    "s0120" to "Image",
    "s0121" to "Model",
    "s0122" to "Add Vehicle",
    "s0123" to "Remarks",
    "s0124" to "Under Review",
    "s0125" to "Them",
    "s0126" to "Place the QR code in the frame",
    "s0127" to "Translate text to",
    "s0128" to "Residence",
    "s0129" to "Reimbursed",
    "s0130" to "Average Rating",
    "s0131" to "Pending Approval",
    "s0132" to "WeChat",
    "s0133" to "WeChat ID",
    "s0134" to "Forgot password?",
    "s0135" to "I have read and agree to",
    "s0136" to "Scan Result",
    "s0137" to "Scan",
    "s0138" to "Submit Reimbursement",
    "s0139" to "Holiday",
    "s0140" to "New Password",
    "s0141" to "No payment needed",
    "s0142" to "Date",
    "s0143" to "Date: ",
    "s0144" to "Stellar Elite · Company Contact",
    "s0145" to "Stellar Elite · Salary Settlement",
    "s0146" to "Yes",
    "s0147" to "More",
    "s0148" to "Updated",
    "s0149" to "Update",
    "s0150" to "Some required fields are missing",
    "s0151" to "Valid",
    "s0152" to "Incomplete",
    "s0153" to "View Application",
    "s0154" to "REGISTER",
    "s0155" to "Register",
    "s0156" to "Failed to save registration: ",
    "s0157" to "Car Wash",
    "s0158" to "Transactions",
    "s0159" to "Transaction Records",
    "s0160" to "Light",
    "s0161" to "Dark",
    "s0162" to "Clear Chat History",
    "s0163" to "Version Update",
    "s0164" to "Generated at: ",
    "s0165" to "Request Handover",
    "s0166" to "Request Leave",
    "s0167" to "Leave Days Requested",
    "s0168" to "Application Type",
    "s0169" to "Log In",
    "s0170" to "Destination",
    "s0171" to "Type",
    "s0172" to "End Date",
    "s0173" to "To",
    "s0174" to "Chat Details",
    "s0175" to "Letters / numbers / symbols, at least 8 characters",
    "s0176" to "Failed to set new password",
    "s0177" to "Voice Call Notifications",
    "s0178" to "Leave",
    "s0179" to "Enter amount",
    "s0180" to "Please select",
    "s0181" to "Pickup",
    "s0182" to "Follow System",
    "s0183" to "Variant",
    "s0184" to "Transfer Amount",
    "s0185" to "Enter Specific Amount",
    "s0186" to "Log Out",
    "s0187" to "Log Out Device",
    "s0188" to "Optional · letters / numbers / punctuation",
    "s0189" to "Select Document",
    "s0190" to "Email",
    "s0191" to "Failed to send reset email",
    "s0192" to "Wallet Balance",
    "s0193" to "Link copied",
    "s0194" to "Flashlight",
    "s0195" to " *Required",
    "s0196" to " items",
    "s0197" to " trips · Salary ",
    "s0198" to " days · ",
    "s0199" to " notifications?",
    "s0200" to " sec",
    "s0201" to " items",
    "s0202" to "%s uploaded",
    "s0203" to "1  From",
    "s0204" to "2  To",
    "s0205" to "3  Transfer Amount",
    "s0206" to "4  Other Details",
    "s0207" to "5 trips settled",
    "s0208" to "6-digit code",
    "s0209" to "6 trips settled",
    "s0210" to "JXY 8888 · Toyota Innova",
    "s0211" to "JXY 8888 fuel",
    "s0212" to "Johor Bahru, Johor",
    "s0213" to "VKL 5566 · Nissan Serena",
    "s0214" to "VKL 5566 car wash",
    "s0215" to "WMB 2233 · Honda CR-V",
    "s0216" to "WMB 2233 toll",
    "s0217" to "[Image x",
    "s0218" to "[Photos & Videos] ",
    "s0219" to "\"Download the Stellar Elite app and place orders on our platform",
    "s0220" to "e-statement PDF saved to Downloads/XYZL",
    "s0221" to "— Premium Invitation · Supreme Journey —",
    "s0222" to "⚠️ Required fields are missing: ",
    "s0223" to "KYC Verification Compliance Terms",
    "s0224" to "End User Software License",
    "s0225" to "End User Software License Agreement",
    "s0226" to "\" chat history?",
    "s0227" to "Handover",
    "s0228" to "Upload failed",
    "s0229" to "Upload failed: ",
    "s0230" to "Upload Passport",
    "s0231" to "Upload Receipt",
    "s0232" to "Upload Document",
    "s0233" to "Upload Malaysian Passport",
    "s0234" to "Upload Malaysian Driving License",
    "s0235" to "Upload Driving License",
    "s0236" to "Download QR Code",
    "s0237" to "Business",
    "s0238" to "Business Location",
    "s0239" to "Passwords do not match",
    "s0240" to "Handle user and driver data strictly under Malaysian regulations",
    "s0241" to "Personal info updated",
    "s0242" to "Personal reason",
    "s0243" to "Personal Details",
    "s0244" to "China Website",
    "s0245" to "Passenger · Mr. Zhang",
    "s0246" to "Passenger · Ms. Li",
    "s0247" to "Passenger · Mr. Wang",
    "s0248" to "Passenger · Ms. Chen",
    "s0249" to "Learn More",
    "s0250" to "QR Code",
    "s0251" to "QR code saved to gallery",
    "s0252" to "Transaction Records",
    "s0253" to "Transactions: ",
    "s0254" to "Transaction No.",
    "s0255" to "Transaction No.: ",
    "s0256" to "Transaction No.: %s\n",
    "s0257" to "Transaction Details",
    "s0258" to "Passengers",
    "s0259" to "Camera upload only",
    "s0260" to "Today's MYR Collection",
    "s0261" to "Today's MYR Collection: ",
    "s0262" to "Today's SGD Collection",
    "s0263" to "Today's SGD Collection: ",
    "s0264" to "Today's Trips: ",
    "s0265" to "Today's Reimbursement: ",
    "s0266" to "Today's Salary · 6 trips",
    "s0267" to "Today's Salary: ",
    "s0268" to "Payment Date",
    "s0269" to "Collection",
    "s0270" to "Collection ",
    "s0271" to "MYR Collection",
    "s0272" to "SGD Collection",
    "s0273" to "SGD Collection",
    "s0274" to "Collection Account",
    "s0275" to "Fare Collection · Mr. Zhang",
    "s0276" to "Fare Collection · Ms. Li",
    "s0277" to "Fare Collection · Mr. Wang",
    "s0278" to "Fare Collection · Ms. Chen",
    "s0279" to "MYR Collection",
    "s0280" to "Rest",
    "s0281" to "Rest ",
    "s0282" to "On Leave",
    "s0283" to "Accommodation",
    "s0284" to "You are already friends",
    "s0285" to "Play messages through earpiece",
    "s0286" to "Use separate send button",
    "s0287" to "e.g. WXY 1234",
    "s0288" to "Leveraging Johor Bahru's location, we focus on Malaysia ↔ Singapore cross-border transfers, chartered rides, and scheduled pickups. The platform connects booking, dispatch, driver scheduling, verification, trip tracking, and online payment.",
    "s0289" to "Save failed. Try again later.",
    "s0290" to "Save failed. Check gallery permission and try again.",
    "s0291" to "Save failed: ",
    "s0292" to "Keep original photos and videos viewed",
    "s0293" to "Insurance Certificate",
    "s0294" to "Edit",
    "s0295" to "Duty Records",
    "s0296" to "Duty details PDF saved to Downloads/XYZL",
    "s0297" to "Duty_Details_",
    "s0298" to "On-duty Vehicle",
    "s0299" to "Stop Accepting Orders",
    "s0300" to "Select All",
    "s0301" to "Company Entity",
    "s0302" to "Company Holiday",
    "s0303" to "128 reviews",
    "s0304" to "Close",
    "s0305" to "Other",
    "s0306" to "Other Login Methods",
    "s0307" to "Other Details",
    "s0308" to "Share My QR Code",
    "s0309" to "After sharing your QR code, others can add you as a contact. After sharing your link, others can start chatting with you.",
    "s0310" to "Create Password",
    "s0311" to "This record will no longer be shown.",
    "s0312" to "Delete failed. Try again later.",
    "s0313" to "Delete Device Record",
    "s0314" to "Delete Notification",
    "s0315" to "Expiry Date",
    "s0316" to "Expiry date is required (YYYY-MM-DD, select above). Upload a clear front photo with all four corners and readable text; status becomes Pending Review after submission.",
    "s0317" to "Remaining Leave Days",
    "s0318" to "Remaining Days",
    "s0319" to "Add Friend",
    "s0320" to "Fuel · JXY 8888",
    "s0321" to "Please accept the agreement",
    "s0322" to "Includes photos, videos, and files.",
    "s0323" to "Current Password",
    "s0324" to "Current password is incorrect",
    "s0325" to "Message…",
    "s0326" to "New version %s found. Update to continue.",
    "s0327" to "New version v%s found",
    "s0328" to "Send",
    "s0329" to "Send Contact Card",
    "s0330" to "Send Message",
    "s0331" to "Send Reset Email",
    "s0332" to "Deselect All",
    "s0333" to "Available Balance (%s)",
    "s0334" to "The driver was punctual, the car was clean, and the ride was smooth. Satisfied!",
    "s0335" to "In-app message banner",
    "s0336" to "When the app is open",
    "s0337" to "When the app is closed",
    "s0338" to "Total: %s",
    "s0339" to "Kuala Lumpur → Penang · Airport Drop-off",
    "s0340" to "KLIA → Singapore trip collection",
    "s0341" to "KLIA → Johor Bahru trip collection",
    "s0342" to "No",
    "s0343" to "Earpiece",
    "s0344" to "The brand list is empty (no active brands in se_vehicle_brands). Ask the admin to add brands first.",
    "s0345" to "Select both brand and model",
    "s0346" to "International Website",
    "s0347" to "Photos & Videos",
    "s0348" to "When translating in chats, web pages, or images, text will be translated into the selected language.",
    "s0349" to "Processing",
    "s0350" to "Remarks: ",
    "s0351" to "Remarks: %s\n",
    "s0352" to "Copy Link",
    "s0353" to "Adult",
    "s0354" to "Female",
    "s0355" to "Friend request sent",
    "s0356" to "96% positive",
    "s0357" to "Completed Trips",
    "s0358" to "Submit",
    "s0359" to "Official",
    "s0360" to "Family matter, need two days off",
    "s0361" to "Password updated. Use the new password next time.",
    "s0362" to "Password (not meeting requirements)",
    "s0363" to "Received by recipient",
    "s0364" to "Awaiting recipient",
    "s0365" to "Recipient has no wallet account yet",
    "s0366" to "Conversation",
    "s0367" to "Export failed. Try again later.",
    "s0368" to "Navigate to",
    "s0369" to "Child",
    "s0370" to "Too many attempts. Try again later.",
    "s0371" to "Uploaded",
    "s0372" to "Receipt uploaded",
    "s0373" to "Leave Days Taken",
    "s0374" to "Friend request already sent, awaiting confirmation",
    "s0375" to "Copied",
    "s0376" to "A duplicate record already exists",
    "s0377" to "Rejected",
    "s0378" to "Authorized Vehicles",
    "s0379" to "Submitted",
    "s0380" to "Already up to date",
    "s0381" to "Logged in",
    "s0382" to "Logged-in Devices",
    "s0383" to "Bound",
    "s0384" to "Read",
    "s0385" to "Days Abroad",
    "s0386" to "Expired · ",
    "s0387" to "Logged out",
    "s0388" to "Logged-out Devices",
    "s0389" to "Selected",
    "s0390" to "Selected ",
    "s0391" to "Muted",
    "s0392" to "Rejected",
    "s0393" to "City Charter 4 Hours",
    "s0394" to "Putrajaya → Genting Highlands",
    "s0395" to "Account",
    "s0396" to "Account copied to clipboard",
    "s0397" to "Account Number",
    "s0398" to "Help & About",
    "s0399" to "Help & Feedback",
    "s0400" to "The platform only provides trip information dispatch and order matching.",
    "s0401" to "Platform Policies & Agreements",
    "s0402" to "The platform does not own or operate any vehicles; all vehicles and drivers are registered partners reviewed by the platform.",
    "s0403" to "When enabled, original photos and videos you sent or viewed are kept. Media from before enabling is unaffected.",
    "s0404" to "When enabled, dark mode follows the system setting.",
    "s0405" to "When enabled, all received chat messages are translated automatically.",
    "s0406" to "When enabled, the send button on the keyboard is replaced with a newline key.",
    "s0407" to "Start Accepting Orders",
    "s0408" to "Expense Date",
    "s0409" to "Affiliated Company",
    "s0410" to "Assigned Vehicle",
    "s0411" to "Current Task",
    "s0412" to "Current Location (location unavailable)",
    "s0413" to "Current Balance (%s)",
    "s0414" to "Current Version",
    "s0415" to "Current version %s is up to date.",
    "s0416" to "Pickup Time",
    "s0417" to "Drop-off Time",
    "s0418" to "Monthly Reimbursement",
    "s0419" to "Monthly Salary",
    "s0420" to "Pending Approval",
    "s0421" to "Pending Review",
    "s0422" to "Vehicles Pending Verification",
    "s0423" to "Must be 8-14 characters",
    "s0424" to "Must contain 1 uppercase letter",
    "s0425" to "Must contain 1 lowercase letter",
    "s0426" to "Must contain 1 number",
    "s0427" to "Must contain 1 special character",
    "s0428" to "Forgot Password",
    "s0429" to "We are committed to local compliance:",
    "s0430" to "Sent by Us",
    "s0431" to "Pending Send",
    "s0432" to "I cannot obtain the owner's IC",
    "s0433" to "Me",
    "s0434" to "My QR Code",
    "s0435" to "My Info",
    "s0436" to "My Current Location",
    "s0437" to "My Vehicles",
    "s0438" to "Company",
    "s0439" to "Company: ",
    "s0440" to "All platform drivers must submit identity documents and complete KYC verification.",
    "s0441" to "Manual Selection",
    "s0442" to "Print",
    "s0443" to "Print PDF",
    "s0444" to "Print e-statement",
    "s0445" to "Open",
    "s0446" to "Scan QR Code",
    "s0447" to "Scan to Add Friend",
    "s0448" to "Speaker",
    "s0449" to "Your personal profile was not found (no record in se_personal_profiles).",
    "s0450" to "Your driver profile was not found (no record in se_driver_profiles).",
    "s0451" to "Driver profile not found; documents cannot be registered.",
    "s0452" to "Your account was not found (no record in se_account_members).",
    "s0453" to "Driver profile not found; document photos were not registered.",
    "s0454" to "Order not found",
    "s0455" to "Mark all notifications as read",
    "s0456" to "Passport",
    "s0457" to "Passport Photo",
    "s0458" to "Reimbursement Type",
    "s0459" to "Reimbursement type: %s",
    "s0460" to "Camera / Gallery / Document",
    "s0461" to "Take a photo or choose an ID photo from gallery",
    "s0462" to "Drag the slider below to set the font size",
    "s0463" to "Drag the slider below to set the font size. This changes the font size across chats and the app. If you have issues or feedback, contact the official team.",
    "s0464" to "Hang Up",
    "s0465" to "Hold to Talk",
    "s0466" to "Vibration",
    "s0467" to "Accepting Orders",
    "s0468" to "Order Time",
    "s0469" to "Submission failed. Try again later.",
    "s0470" to "Submission failed: ",
    "s0471" to "Withdraw",
    "s0472" to "Cancel Application",
    "s0473" to "Play",
    "s0474" to "Payments & Collections",
    "s0475" to "Camera / Gallery supported",
    "s0476" to "Receipt",
    "s0477" to "Receipt Proof",
    "s0478" to "Receive",
    "s0479" to "Amount Received",
    "s0480" to "Overall good; I will recommend it to friends.",
    "s0481" to "Singapore → Johor Bahru trip collection",
    "s0482" to "New password cannot be the same as the current password",
    "s0483" to "Johor Bahru → KLIA trip collection",
    "s0484" to "New Transfer",
    "s0485" to "Unable to save",
    "s0486" to "Unable to add this person as a friend",
    "s0487" to "Date Time",
    "s0488" to "Date & Time",
    "s0489" to "Date & Time: ",
    "s0490" to "Date & Time: %s\n",
    "s0491" to "Stellar",
    "s0492" to "Stellar Driver",
    "s0493" to "Stellar Elite Travel & Transport",
    "s0494" to "Stellar Elite · Transaction Receipt\n",
    "s0495" to "Stellar Elite Driver",
    "s0496" to "Stellar Elite Driver · e-Statement",
    "s0497" to "Stellar Elite Driver · Duty Details",
    "s0498" to "Stellar Elite · My QR Code",
    "s0499" to "Stellar Elite is a business unit of Stellar Group, which also covers F&B, technology development, and digital systems. Its tech team builds the platform, driver app, passenger app, and admin system.",
    "s0500" to "© Stellar Elite. All rights reserved.",
    "s0501" to "Stellar Elite Travel & Transport Sdn. Bhd.",
    "s0502" to "Stellar Elite Travel, part of STELLAR ELITE ENTERPRISE registered in Johor, Malaysia, is a cross-border mobility platform serving Malaysia and Singapore.",
    "s0503" to "Yesterday's Salary · 5 trips",
    "s0504" to "Vehicle Owner",
    "s0505" to "No logged-in devices",
    "s0506" to "No logged-out devices",
    "s0507" to "No verified vehicles",
    "s0508" to "No vehicles pending verification",
    "s0509" to "No messages",
    "s0510" to "No contacts. Tap New Transfer to search by account number.",
    "s0511" to "No trips",
    "s0512" to "No records",
    "s0513" to "No reviews",
    "s0514" to "No active tasks",
    "s0515" to "No notifications",
    "s0516" to "No invite code",
    "s0517" to "No wallet accounts",
    "s0518" to "Change",
    "s0519" to "Change On-duty Vehicle",
    "s0520" to "Updating…",
    "s0521" to "Update failed",
    "s0522" to "Update KYC Verification",
    "s0523" to "Update is unavailable",
    "s0524" to "Last Login",
    "s0525" to "Latest Version",
    "s0526" to "Recent Reviews of Me",
    "s0527" to "Recent Reviews of This Driver",
    "s0528" to "Valid · until ",
    "s0529" to "Not uploaded",
    "s0530" to "WeChat not provided",
    "s0531" to "Phone not provided",
    "s0532" to "Account not found",
    "s0533" to "Not checked",
    "s0534" to "Unknown Device",
    "s0535" to "Not bound",
    "s0536" to "No on-duty vehicle set",
    "s0537" to "No QR code detected",
    "s0538" to "Unread",
    "s0539" to "Airport Pickup",
    "s0540" to "Ms. Li",
    "s0541" to "From",
    "s0542" to "From: ",
    "s0543" to "From: %s\n",
    "s0544" to "Release to Send · ",
    "s0545" to "Search Chat",
    "s0546" to "Mark as Read",
    "s0547" to "Checking…",
    "s0548" to "Check failed",
    "s0549" to "Check Version",
    "s0550" to "Downloading the new version, please wait…",
    "s0551" to "Receiving new orders",
    "s0552" to "Updating to v%s",
    "s0553" to "Recognizing…",
    "s0554" to "Reading binding status…",
    "s0555" to "No vehicles available",
    "s0556" to "No account?",
    "s0557" to "No matching chat history",
    "s0558" to "No permission to submit; check the database policy.",
    "s0559" to "No permission to modify this record",
    "s0560" to "No permission to log out this device",
    "s0561" to "No QR code detected. Try another image.",
    "s0562" to "No microphone permission; cannot record.",
    "s0563" to "Registered Address",
    "s0564" to "Registration failed. Please try again.",
    "s0565" to "Registration failed: could not get login state. Please try again.",
    "s0566" to "Registration Successful",
    "s0567" to "Registration No.",
    "s0568" to "Tamil",
    "s0569" to "Car Wash · VKL 5566",
    "s0570" to "Mute Messages",
    "s0571" to "Message Sound",
    "s0572" to "Dark / Light Mode",
    "s0573" to "Dark Mode",
    "s0574" to "Clear all notifications (soft delete, recoverable)",
    "s0575" to "Clearing only hides chats for you (user_cleared_at); server records are not deleted.",
    "s0576" to "All local chat history will be deleted and cannot be recovered.",
    "s0577" to "Clear Current Chat",
    "s0578" to "Clear or export applies to all chat history saved on this device.",
    "s0579" to "Clear Notifications",
    "s0580" to "Slide to Confirm Pickup",
    "s0581" to "Slide to Confirm Drop-off",
    "s0582" to "Tap Date to select a day, or tap again to change.",
    "s0583" to "Click the download link below\"",
    "s0584" to "Tap the button on the right to start accepting orders",
    "s0585" to "Tap to view (location off)",
    "s0586" to "Tap to set avatar",
    "s0587" to "Status: ",
    "s0588" to "Status: %s\n",
    "s0589" to "Use",
    "s0590" to "Apply",
    "s0591" to "Apply ",
    "s0592" to "Application for \"%s\" submitted.",
    "s0593" to "Handover application for %s of %s submitted.",
    "s0594" to "Expense funds application for %s %s submitted.",
    "s0595" to "Application Info",
    "s0596" to "Request Salary",
    "s0597" to "Application Date",
    "s0598" to "Application Time",
    "s0599" to "Application Details",
    "s0600" to "Application Details",
    "s0601" to "Male",
    "s0602" to "LOG IN",
    "s0603" to "Log in to save personal info",
    "s0604" to "Log in to submit vehicles",
    "s0605" to "Log in to update KYC verification",
    "s0606" to "Session refresh failed. Please log in again.",
    "s0607" to "Got it",
    "s0608" to "Delete selected ",
    "s0609" to "Clear chat history with \"",
    "s0610" to "Clear all notifications?",
    "s0611" to "Log out of this account?",
    "s0612" to "Passwords do not match",
    "s0613" to "Confirm Submission",
    "s0614" to "Confirm Changes",
    "s0615" to "Confirm Update",
    "s0616" to "Confirm Application",
    "s0617" to "Confirm Transfer",
    "s0618" to "Later",
    "s0619" to "Try again later",
    "s0620" to "Update Now",
    "s0621" to "Third-party binding coming soon",
    "s0622" to "Type: ",
    "s0623" to "Type: %s\n",
    "s0624" to "System Message Notifications",
    "s0625" to "OS Version",
    "s0626" to "Binding failed: ",
    "s0627" to "Binding status is read from se_user_oauth_bindings; third-party login requires Apple / Facebook / WeChat SDK integration before authorization.",
    "s0628" to "Continue",
    "s0629" to "Maintenance",
    "s0630" to "Edit",
    "s0631" to "Network error or check not supported. Try again later.",
    "s0632" to "Network error. Check your connection and try again.",
    "s0633" to "Pin",
    "s0634" to "Pin Chat",
    "s0635" to "Translate to ",
    "s0636" to "Contact Card",
    "s0637" to "Contact Us",
    "s0638" to "Contact Info",
    "s0639" to "Auto-download content viewed on other devices",
    "s0640" to "Auto-generated, customizable, unique",
    "s0641" to "Auto-translate received chat messages",
    "s0642" to "Luggage",
    "s0643" to "Trip Completed",
    "s0644" to "Trip Salary",
    "s0645" to "Trip Details",
    "s0646" to "Video",
    "s0647" to "Video Call",
    "s0648" to "Video calls coming soon",
    "s0649" to "Order No.",
    "s0650" to "Order No.: ",
    "s0651" to "Order No.: %s\n",
    "s0652" to "Failed to save device info: ",
    "s0653" to "Device Name",
    "s0654" to "Device Type",
    "s0655" to "Device Details",
    "s0656" to "Changing this affects the font size of chats and the whole app. If you have any issues or feedback, contact the official team.",
    "s0657" to "Set Avatar",
    "s0658" to "Documents submitted, pending review",
    "s0659" to "Failed to register document photos: ",
    "s0660" to "Reviews",
    "s0661" to "This QR code is valid for %s seconds",
    "s0662" to "This email is registered but the password is incorrect. If this is you, log in directly; tap Forgot Password to reset.",
    "s0663" to "Voice",
    "s0664" to "Voice Call",
    "s0665" to "Voice calls coming soon",
    "s0666" to "Take or choose an ID photo first",
    "s0667" to "Re-enter password",
    "s0668" to "Ask your company for an authorized vehicle",
    "s0669" to "Please update to the latest version",
    "s0670" to "Enter an 8-14 character password",
    "s0671" to "Enter a reason",
    "s0672" to "Enter current password",
    "s0673" to "Enter remarks (optional)",
    "s0674" to "Enter password",
    "s0675" to "Enter the recipient's account number",
    "s0676" to "Enter phone number / email",
    "s0677" to "Enter the email used at registration; we will send a reset link.",
    "s0678" to "Select a type",
    "s0679" to "Select the document expiry date (YYYY-MM-DD)",
    "s0680" to "Select a vehicle",
    "s0681" to "Re-enter the current password, or tap Forgot Password to reset by email.",
    "s0682" to "Cannot read the selected file. Try another one.",
    "s0683" to "Incorrect account or password",
    "s0684" to "Account created. Log in with the email and password you just used.",
    "s0685" to "Account Management",
    "s0686" to "Incomplete information",
    "s0687" to "Timed out",
    "s0688" to "Cross-border",
    "s0689" to "Cross-border charter, travel booking, and online mobility platform operations",
    "s0690" to "Road Tax Certificate",
    "s0691" to "Familiar with the route and easy to communicate with; wait was slightly long.",
    "s0692" to "ID Card",
    "s0693" to "ID Photo",
    "s0694" to "Owner's Malaysian IC",
    "s0695" to "Front Row Photo",
    "s0696" to "Front Photo",
    "s0697" to "Vehicle Registration Certificate",
    "s0698" to "Rear Row Photo",
    "s0699" to "Rear Photo",
    "s0700" to "Plate Number",
    "s0701" to "Plate Number",
    "s0702" to "Vehicle",
    "s0703" to "Vehicle registered, but documents failed: ",
    "s0704" to "Vehicle Photos",
    "s0705" to "Vehicle registration failed: no vehicle record ID",
    "s0706" to "Vehicle Certificate",
    "s0707" to "Vehicle info submitted, pending review",
    "s0708" to "Transfer info is incomplete. Go back and check.",
    "s0709" to "Transfer Failed",
    "s0710" to "Transfer submission failed",
    "s0711" to "Transfer Date",
    "s0712" to "Back to Login",
    "s0713" to "Expiry date not filled",
    "s0714" to "No official notifications",
    "s0715" to "No chat conversations",
    "s0716" to "No file selected",
    "s0717" to "This is not a Stellar Elite QR code",
    "s0718" to "This ID is already taken. Choose another.",
    "s0719" to "This vehicle (same plate) is already registered",
    "s0720" to "No QR code found in this image. Try another one.",
    "s0721" to "This is your own QR code",
    "s0722" to "Late",
    "s0723" to "After logging out, security verification is required the next time you log in on this device.",
    "s0724" to "Logout failed. Try again later.",
    "s0725" to "Optional · fill in to join the company; leave blank for personal",
    "s0726" to "Select Brand",
    "s0727" to "Select Model",
    "s0728" to "Select Variant",
    "s0729" to "General",
    "s0730" to "Notification Actions",
    "s0731" to "Continue with Apple",
    "s0732" to "Continue with Facebook",
    "s0733" to "Continue with WeChat",
    "s0734" to "By Date & Time",
    "s0735" to "There is only one type of invite code: it belongs to an account. Individuals/sole proprietors/companies show their own; drivers/support/customers show the affiliated company's. When a customer shares it, friends using the same code join the same company.",
    "s0736" to "Invite code copied",
    "s0737" to "Email",
    "s0738" to "Email not verified. Open the verification link in your inbox first.",
    "s0739" to "Resend",
    "s0740" to "Resend ",
    "s0741" to "Reset Password",
    "s0742" to "Reset email sent to %s. Open the link in your inbox, then return to the app to set a new password.",
    "s0743" to "Amount (%s)",
    "s0744" to "Amount (%s): %s\n",
    "s0745" to "Amount: ",
    "s0746" to "Link",
    "s0747" to "Link Sharing",
    "s0748" to "Link copied. Opening Instagram…",
    "s0749" to "Link copied. Opening WeChat…",
    "s0750" to "Mrs. Chen",
    "s0751" to "Mr. Chen",
    "s0752" to "Privacy info updated",
    "s0753" to "Group Background",
    "s0754" to "Mute",
    "s0755" to "Salary Advance",
    "s0756" to "Font Size Preview",
    "s0757" to "Malacca Day Tour",
    "s0758" to "Malay",
    "s0759" to "Driving License",
    "s0760" to "License Photo",
    "s0761" to "Verification Date & Time",
    "s0762" to "Verification Code",
    "s0763" to "Verification code sent to",
    "s0764" to ", invite code: \"",
    "s0765" to ": No file selected",
    "s0766" to ": Cannot read local file",
    "tabChat" to "Chat",
    "tabFinance" to "Finance",
    "tabHome" to "Home",
    "tabProfile" to "Me",
    "tabTrips" to "Trips",
    "translation" to "Translation",
))

object LocalStrings {
    val current: AppStrings get() = appStrings(AppLanguageStore.language)
}
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ZH }

fun appStrings(lang: AppLanguage): AppStrings = if (lang == AppLanguage.EN) EnStrings else ZhStrings
