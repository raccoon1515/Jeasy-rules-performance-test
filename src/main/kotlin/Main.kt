import org.jeasy.rules.api.Facts
import org.jeasy.rules.api.RuleListener
import org.jeasy.rules.api.Rules
import org.jeasy.rules.api.RulesEngineListener
import org.jeasy.rules.core.DefaultRulesEngine
import org.jeasy.rules.core.RuleBuilder
import org.jeasy.rules.jexl.JexlRule
import org.jeasy.rules.mvel.MVELRule
import org.jeasy.rules.spel.SpELRule
import org.jeasy.rules.support.composite.UnitRuleGroup
import java.lang.Exception
import java.util.function.Consumer
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {

    val trap = Trap(
        id = "100",
        oid = "1.1.1.1.1.1.1",
        data = mapOf(
            "2.2.2.2.2.2.2" to "50",
            "3.3.3.3.3.3.3" to "100",
            "4.4.4.4.4.4.4" to "200"
        )
    )

    val eventConsumer = EventConsumer()

    val rulesEngine = DefaultRulesEngine()
    rulesEngine.registerRulesEngineListener(CustomRuleEngineListener)
    rulesEngine.registerRuleListener(CustomRuleListener)

    val facts = Facts().apply {
        put("trap", trap)
        put("eventConsumer", eventConsumer)
        put("oid", "1.1.1.1.1.1.1")
        put("trapId", trap.id)
    }

    val trapCount = 1000

//    val jexlSingleRuleMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeJexlSingleRule(facts, rulesEngine)
//        }
//    }
//
//    val coreSingleRuleMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeCoreSingleRule(facts, rulesEngine)
//        }
//    }
//
//    val spelSingleCoreMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeSpelSingleRule(facts, rulesEngine)
//        }
//    }
//
//    val mvelSingleCoreMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeMvelSingleRule(facts, rulesEngine)
//        }
//    }
//
//    println("$trapCount trap SINGLE rule handling with JEXL time: $jexlSingleRuleMeasuredTime ms.")
//    println("$trapCount trap SINGLE rule handling with CORE time: $coreSingleRuleMeasuredTime ms.")
//    println("$trapCount trap SINGLE rule handling with SPEL time: $spelSingleCoreMeasuredTime ms.")
//    println("$trapCount trap SINGLE rule handling with MVEL time: $mvelSingleCoreMeasuredTime ms.")


//    val jexlCompositeRuleMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeJexlCompositeRule(facts, rulesEngine)
//        }
//    }

//    val coreCompositeRuleMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
            executeCoreCompositeRule(facts, rulesEngine)
//        }
//    }

//    val spelCompositeCoreMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeSpelCompositeRule(facts, rulesEngine)
//        }
//    }

//    val mvelCompositeCoreMeasuredTime = measureTimeMillis {
//        repeat(trapCount) {
//            executeMvelCompositeRule(facts, rulesEngine)
//        }
//    }

//    println("$trapCount trap COMPOSITE rule handling with JEXL time: $jexlCompositeRuleMeasuredTime ms.")
//    println("$trapCount trap COMPOSITE rule handling with CORE time: $coreCompositeRuleMeasuredTime ms.")
//    println("$trapCount trap COMPOSITE rule handling with SPEL time: $spelCompositeCoreMeasuredTime ms.")
//    println("$trapCount trap COMPOSITE rule handling with MVEL time: $mvelCompositeCoreMeasuredTime ms.")
}

fun executeJexlSingleRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val rules = SingleRuleRepository.getOne().let {
        JexlRule()
            .name(it.name)
            .description(it.description)
            .`when`(it.condition)
            .then("eventConsumer.accept(trap.getId())")
    }

    rulesEngine.fire(Rules(rules), facts)
}

fun executeJexlCompositeRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val ruleGroup = UnitRuleGroup("myGroup")

    val rules = listOf(
        Rule(name = "oid equals", condition = "trap.getOid().equals(\"1.1.1.1.1.1.1\")"),
        Rule(name = "sub oid value equals", condition = "trap.getData().get(\"2.2.2.2.2.2.2\").equals(\"50\")"),
        Rule(name = "sub oid 2 value equals", condition = "trap.getData().get(\"3.3.3.3.3.3.3\").equals(\"100\")"),
    )

    rules.forEach {
        ruleGroup.addRule(
            JexlRule()
                .name(it.name)
                .description(it.description)
                .`when`(it.condition)
                .then("eventConsumer.accept(trap.getId())")
        )
    }

    rulesEngine.fire(Rules(ruleGroup), facts)
}

fun executeMvelSingleRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val rules = SingleRuleRepository.getOne().let {
        MVELRule()
            .name(it.name)
            .description(it.description)
            .`when`(it.condition)
            .then("eventConsumer.accept(trap.getId())")
    }

    rulesEngine.fire(Rules(rules), facts)
}

fun executeMvelCompositeRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val ruleGroup = UnitRuleGroup("myGroup")

    val rules = listOf(
        Rule(name = "oid equals", condition = "trap.getOid().equals(\"1.1.1.1.1.1.1\")"),
        Rule(name = "sub oid value equals", condition = "trap.getData().get(\"2.2.2.2.2.2.2\").equals(\"50\")"),
        Rule(name = "sub oid 2 value equals", condition = "trap.getData().get(\"3.3.3.3.3.3.3\").equals(\"100\")"),
    )

    rules.forEach {
        ruleGroup.addRule(
            MVELRule()
                .name(it.name)
                .description(it.description)
                .`when`(it.condition)
                .then("eventConsumer.accept(trap.getId())")
        )
    }

    rulesEngine.fire(Rules(ruleGroup), facts)
}

fun executeSpelSingleRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val rules = SingleRuleRepository.getOne().let {
        SpELRule()
            .name(it.name)
            .description(it.description)
            .`when`("#{[trap].oid == '1.1.1.1.1.1.1'}")
            .then("#{['eventConsumer'].accept(['trap'].id)}")
    }

    rulesEngine.fire(Rules(rules), facts)
}

fun executeSpelCompositeRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val ruleGroup = UnitRuleGroup("myGroup")

    val rules = listOf(
        Rule(name = "oid equals", condition = "#{[trap].oid == '1.1.1.1.1.1.1'}"),
        Rule(name = "sub oid value equals", condition = "#{[trap].data['2.2.2.2.2.2.2'] == '100'}"),
        Rule(name = "sub oid 2 value equals", condition = "#{[trap].data['3.3.3.3.3.3.3'] == '50'}"),
    )

    rules.forEach {
        ruleGroup.addRule(
            SpELRule()
                .name(it.name)
                .description(it.description)
                .`when`(it.condition)
                .then("#{['eventConsumer'].accept(['trap'].id)}")
        )
    }

    rulesEngine.fire(Rules(ruleGroup), facts)
}

fun executeCoreSingleRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val rules = SingleRuleRepository.getOne().let {
        RuleBuilder()
            .name(it.name)
            .description(it.description)
            .`when` { facts -> facts.get<String>("oid") == "1.1.1.1.1.1.1" }
            .then { facts -> facts.get<EventConsumer>("eventConsumer").accept(facts.get("trapId")) }
            .build()
    }

    rulesEngine.fire(Rules(rules), facts)
}

fun executeCoreCompositeRule(facts: Facts, rulesEngine: DefaultRulesEngine) {

    val ruleGroup = UnitRuleGroup("myGroup")

    val rules = listOf(
        RuleBuilder()
            .name("oid equals")
            .`when` { facts -> facts.get<String>("oid") == "1.1.1.1.1.1.1" }
            .then { facts -> facts.get<EventConsumer>("eventConsumer").accept(facts.get("trapId")) }
            .build(),
        RuleBuilder()
            .name("sub oid value equals")
            .`when` { facts -> facts.get<Trap>("trap").data["2.2.2.2.2.2.2"] == "100" }
            .then { facts -> facts.get<EventConsumer>("eventConsumer").accept(facts.get("trapId")) }
            .build(),
        RuleBuilder()
            .name("sub oid 2 value equals")
            .`when` { facts -> facts.get<Trap>("trap").data["3.3.3.3.3.3.3"] == "50" }
            .then { facts -> facts.get<EventConsumer>("eventConsumer").accept(facts.get("trapId")) }
            .build()
    )

    rules.forEach {
        ruleGroup.addRule(it)
    }

    rulesEngine.fire(Rules(ruleGroup), facts)
}

class EventConsumer : Consumer<String> {
    override fun accept(trapId: String) {
        println("Consume trapId: $trapId")
    }
}

data class Trap(
    val id: String,
    val oid: String,
    val data: Map<String, String>
)

data class Rule(
    val name: String,
    val description: String = "",
    val condition: String
)

object SingleRuleRepository {

    private val condition = Rule(name = "High CPU utilization", condition = "trap.getOid().equals(\"1.1.1.1.1.1.1\")")

    fun getOne(): Rule = condition
}

// Listeners

object CustomRuleEngineListener : RulesEngineListener {
    override fun beforeEvaluate(rules: Rules?, facts: Facts?) {
        println("Start handle rule. RULES = $rules. FACTS = $facts")
    }

    override fun afterExecute(rules: Rules?, facts: Facts?) {
        println("Finish handle rule. RULES = $rules. FACTS = $facts")
    }
}

object CustomRuleListener : RuleListener {
    override fun beforeEvaluate(rule: org.jeasy.rules.api.Rule?, facts: Facts?): Boolean {
        println("beforeEvaluate")
        return super.beforeEvaluate(rule, facts)
    }

    override fun afterEvaluate(rule: org.jeasy.rules.api.Rule?, facts: Facts?, evaluationResult: Boolean) {
        println("afterEvaluate")
        super.afterEvaluate(rule, facts, evaluationResult)
    }

    override fun onEvaluationError(rule: org.jeasy.rules.api.Rule?, facts: Facts?, exception: Exception?) {
        println("onEvaluationError")
        exception?.printStackTrace()
        super.onEvaluationError(rule, facts, exception)
    }

    override fun beforeExecute(rule: org.jeasy.rules.api.Rule?, facts: Facts?) {
        println("beforeExecute")
        super.beforeExecute(rule, facts)
    }

    override fun onSuccess(rule: org.jeasy.rules.api.Rule?, facts: Facts?) {
        println("onSuccess")
        super.onSuccess(rule, facts)
    }

    override fun onFailure(rule: org.jeasy.rules.api.Rule?, facts: Facts?, exception: Exception?) {
        println("onFailure")
        exception?.printStackTrace()
        super.onFailure(rule, facts, exception)
    }
}